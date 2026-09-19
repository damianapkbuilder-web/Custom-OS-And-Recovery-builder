package com.example.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.data.engine.BuildResult
import com.example.data.engine.ZipPatcherEngine
import com.example.data.local.AppDatabase
import com.example.data.local.BuildEntity
import com.example.data.model.BuildConfiguration
import com.example.data.repository.BuildRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Android Background Service that manages the lifecycle of:
 * 1. Downloading selected ROM and Recovery ZIP packages.
 * 2. Extracting package contents into a staging workspace.
 * 3. Applying user-selected modifications (Holo GUI Style, Magisk, Debloater, Custom Kernel Flasher, etc.).
 * 4. Repackaging modified files into a new flashable ZIP.
 * 5. Providing real-time progress updates and graceful error handling.
 */
class FirmwareBuildService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var activeBuildJob: Job? = null
    private lateinit var patcherEngine: ZipPatcherEngine
    private lateinit var database: AppDatabase
    private lateinit var repository: BuildRepository

    companion object {
        const val ACTION_START_BUILD = "com.example.service.START_BUILD"
        const val ACTION_CANCEL_BUILD = "com.example.service.CANCEL_BUILD"
        const val CHANNEL_ID = "firmware_build_channel"
        const val NOTIFICATION_ID = 2026

        // Live observable state for UI
        private val _isBuilding = MutableStateFlow(false)
        val isBuilding: StateFlow<Boolean> = _isBuilding.asStateFlow()

        private val _progress = MutableStateFlow(0f)
        val progress: StateFlow<Float> = _progress.asStateFlow()

        private val _statusText = MutableStateFlow("Idle")
        val statusText: StateFlow<String> = _statusText.asStateFlow()

        private val _terminalLogs = MutableStateFlow<List<String>>(emptyList())
        val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

        private val _buildResult = MutableStateFlow<BuildResult?>(null)
        val buildResult: StateFlow<BuildResult?> = _buildResult.asStateFlow()

        private val _buildError = MutableStateFlow<String?>(null)
        val buildError: StateFlow<String?> = _buildError.asStateFlow()

        // Shared current active configuration
        private var currentConfig: BuildConfiguration? = null

        fun startService(context: Context, config: BuildConfiguration) {
            currentConfig = config
            val intent = Intent(context, FirmwareBuildService::class.java).apply {
                action = ACTION_START_BUILD
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    context.startForegroundService(intent)
                } catch (e: Exception) {
                    context.startService(intent)
                }
            } else {
                context.startService(intent)
            }
        }

        fun cancelService(context: Context) {
            val intent = Intent(context, FirmwareBuildService::class.java).apply {
                action = ACTION_CANCEL_BUILD
            }
            context.startService(intent)
        }

        fun clearResult() {
            _buildResult.value = null
            _buildError.value = null
            _progress.value = 0f
            _statusText.value = "Idle"
            _terminalLogs.value = emptyList()
        }
    }

    override fun onCreate() {
        super.onCreate()
        patcherEngine = ZipPatcherEngine(applicationContext)
        database = AppDatabase.getInstance(applicationContext)
        repository = BuildRepository(database.buildDao())
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CANCEL_BUILD -> {
                cancelCurrentBuild()
                stopSelf()
            }
            ACTION_START_BUILD -> {
                val config = currentConfig ?: BuildConfiguration()
                startBuildPipeline(config)
            }
        }
        return START_NOT_STICKY
    }

    private fun startBuildPipeline(config: BuildConfiguration) {
        if (_isBuilding.value) return

        _isBuilding.value = true
        _buildError.value = null
        _buildResult.value = null
        _progress.value = 0.05f
        _statusText.value = "Initializing build service..."
        _terminalLogs.value = emptyList()

        startForegroundNotification("Initializing packaging pipeline...")

        activeBuildJob = serviceScope.launch {
            try {
                val result = patcherEngine.executeModification(
                    config = config,
                    onLog = { logLine ->
                        _terminalLogs.value = _terminalLogs.value + logLine
                    },
                    onProgress = { progressVal, status ->
                        _progress.value = progressVal
                        _statusText.value = status
                        updateNotification(status, (progressVal * 100).toInt())
                    }
                )

                // Save to local Room database
                val entity = BuildEntity(
                    buildUuid = result.buildUuid,
                    timestamp = System.currentTimeMillis(),
                    targetType = result.targetType,
                    softwareName = result.softwareName,
                    softwareVersion = result.softwareVersion,
                    deviceName = result.deviceName,
                    deviceCodename = result.deviceCodename,
                    targetArch = result.targetArch,
                    archDowngradeLabel = result.archDowngradeLabel,
                    featuresSummary = result.featuresSummary,
                    zipFileName = result.zipFileName,
                    zipFilePath = result.zipFile.absolutePath,
                    fileSizeBytes = result.fileSizeBytes,
                    md5Checksum = result.md5Checksum,
                    status = "COMPLETED",
                    buildLog = result.fullLogs
                )
                repository.insertBuild(entity)

                // Enqueue sync record
                repository.enqueueSync(
                    syncKey = result.buildUuid,
                    syncType = "BUILD_MANIFEST",
                    payload = "Build ${result.zipFileName} [${result.md5Checksum}]"
                )

                _buildResult.value = result
                _statusText.value = "Flashable package generated: ${result.zipFileName}"
                _isBuilding.value = false
                updateNotification("Packaging finished successfully!", 100)

            } catch (e: Exception) {
                val errorMsg = e.localizedMessage ?: "Unknown packaging failure"
                _buildError.value = errorMsg
                _statusText.value = "Packaging failed: $errorMsg"
                _terminalLogs.value = _terminalLogs.value + "[ERROR:SERVICE] $errorMsg"
                _isBuilding.value = false
                updateNotification("Build failed: $errorMsg", 0)
            } finally {
                stopForeground(STOP_FOREGROUND_DETACH)
            }
        }
    }

    private fun cancelCurrentBuild() {
        activeBuildJob?.cancel()
        _isBuilding.value = false
        _statusText.value = "Build cancelled by user"
        _terminalLogs.value = _terminalLogs.value + "[SERVICE] Packaging aborted by user request."
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Flashable Build Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows real-time progress of ROM & Recovery download and packaging"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun startForegroundNotification(status: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Recovery & ROM Studio")
            .setContentText(status)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setProgress(100, 5, false)
            .build()
        try {
            startForeground(NOTIFICATION_ID, notification)
        } catch (_: Exception) {
            // Ignore foreground service start restrictions on newer API if running in background
        }
    }

    private fun updateNotification(status: String, progressPercent: Int) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Building Flashable Package")
            .setContentText(status)
            .setSmallIcon(if (progressPercent >= 100) android.R.drawable.stat_sys_download_done else android.R.drawable.stat_sys_download)
            .setProgress(100, progressPercent, progressPercent <= 0)
            .setOngoing(progressPercent < 100)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
