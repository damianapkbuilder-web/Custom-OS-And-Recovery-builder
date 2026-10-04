package com.example.data.downloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
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
 * Android Foreground Service responsible for uninterrupted streaming of large ROMs and Recovery binaries.
 * Manages WakeLocks to prevent CPU sleep during multi-gigabyte downloads and updates system notifications.
 */
class BinaryDownloadService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var downloadJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var engine: StreamDownloaderEngine

    companion object {
        const val ACTION_START_DOWNLOAD = "com.example.downloader.START_DOWNLOAD"
        const val ACTION_CANCEL_DOWNLOAD = "com.example.downloader.CANCEL_DOWNLOAD"
        const val CHANNEL_ID = "binary_downloader_channel"
        const val NOTIFICATION_ID = 4040

        private val _downloadState = MutableStateFlow(DownloadUiSnapshot())
        val downloadState: StateFlow<DownloadUiSnapshot> = _downloadState.asStateFlow()

        private val _terminalLogs = MutableStateFlow<List<String>>(emptyList())
        val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

        private var pendingRequest: DownloadRequest? = null

        fun startDownload(context: Context, request: DownloadRequest) {
            pendingRequest = request
            val intent = Intent(context, BinaryDownloadService::class.java).apply {
                action = ACTION_START_DOWNLOAD
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    context.startForegroundService(intent)
                } catch (_: Exception) {
                    context.startService(intent)
                }
            } else {
                context.startService(intent)
            }
        }

        fun cancelDownload(context: Context) {
            val intent = Intent(context, BinaryDownloadService::class.java).apply {
                action = ACTION_CANCEL_DOWNLOAD
            }
            context.startService(intent)
        }

        fun clearState() {
            _downloadState.value = DownloadUiSnapshot()
            _terminalLogs.value = emptyList()
        }
    }

    override fun onCreate() {
        super.onCreate()
        engine = StreamDownloaderEngine(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CANCEL_DOWNLOAD -> {
                cancelActiveDownload()
                stopSelf()
            }
            ACTION_START_DOWNLOAD -> {
                val req = pendingRequest
                if (req != null) {
                    executeDownloadPipeline(req)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun executeDownloadPipeline(request: DownloadRequest) {
        if (_downloadState.value.status == DownloadStatus.DOWNLOADING) return

        acquireWakeLock()
        _downloadState.value = DownloadUiSnapshot(
            status = DownloadStatus.CONNECTING,
            activeRequest = request,
            statusMessage = "Starting download...",
            logs = listOf("Initializing streaming connection for ${request.fileName}")
        )
        _terminalLogs.value = listOf("[SERVICE] Initiated foreground stream for: ${request.fileName}")

        startForegroundNotification(request.fileName, "Connecting to server...")

        downloadJob = serviceScope.launch {
            try {
                val result = engine.downloadBinary(
                    request = request,
                    onProgress = { progress ->
                        _downloadState.value = _downloadState.value.copy(
                            progress = progress,
                            status = if (_downloadState.value.status == DownloadStatus.VERIFYING_CHECKSUM)
                                DownloadStatus.VERIFYING_CHECKSUM else DownloadStatus.DOWNLOADING
                        )
                        updateNotificationProgress(request.fileName, progress)
                    },
                    onStatusChange = { status, message ->
                        _downloadState.value = _downloadState.value.copy(
                            status = status,
                            statusMessage = message
                        )
                        updateNotificationStatus(request.fileName, message)
                    },
                    onLog = { logLine ->
                        _terminalLogs.value = _terminalLogs.value + logLine
                    }
                )

                if (result.isSuccess) {
                    val snapshot = result.getOrThrow()
                    _downloadState.value = snapshot
                    updateNotificationComplete(request.fileName, true, "Download and verification complete!")
                } else {
                    val err = result.exceptionOrNull()?.localizedMessage ?: "Download failed"
                    _downloadState.value = _downloadState.value.copy(
                        status = DownloadStatus.FAILED,
                        error = err,
                        statusMessage = "Failed: $err"
                    )
                    updateNotificationComplete(request.fileName, false, "Failed: $err")
                }
            } catch (e: Exception) {
                val err = e.localizedMessage ?: "Unexpected stream error"
                _downloadState.value = _downloadState.value.copy(
                    status = DownloadStatus.FAILED,
                    error = err,
                    statusMessage = "Error: $err"
                )
                updateNotificationComplete(request.fileName, false, "Error: $err")
            } finally {
                releaseWakeLock()
                stopForeground(STOP_FOREGROUND_DETACH)
            }
        }
    }

    private fun cancelActiveDownload() {
        downloadJob?.cancel()
        _downloadState.value = _downloadState.value.copy(
            status = DownloadStatus.CANCELLED,
            statusMessage = "Download cancelled by user"
        )
        _terminalLogs.value = _terminalLogs.value + "[SERVICE] Download cancelled by user."
        releaseWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = powerManager?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "RecoveryRomStudio:BinaryDownloaderWakeLock"
            )?.apply {
                // Set safety timeout of 2 hours for massive multi-gigabyte downloads
                acquire(2 * 60 * 60 * 1000L)
            }
        } catch (_: Exception) {}
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (_: Exception) {}
        wakeLock = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "ROM & Recovery Downloader",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows real-time streaming progress, network speed, and checksum verification."
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun startForegroundNotification(title: String, initialStatus: String) {
        val cancelIntent = Intent(this, BinaryDownloadService::class.java).apply {
            action = ACTION_CANCEL_DOWNLOAD
        }
        val cancelPendingIntent = PendingIntent.getService(
            this,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val activityIntent = Intent(this, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(initialStatus)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentIntent(activityPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", cancelPendingIntent)
            .setOngoing(true)
            .setProgress(100, 0, true)
            .build()

        try {
            startForeground(NOTIFICATION_ID, notification)
        } catch (_: Exception) {}
    }

    private fun updateNotificationProgress(title: String, progress: DownloadProgress) {
        val cancelIntent = Intent(this, BinaryDownloadService::class.java).apply {
            action = ACTION_CANCEL_DOWNLOAD
        }
        val cancelPendingIntent = PendingIntent.getService(
            this,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val speedAndEta = "${progress.formattedDownloaded} / ${progress.formattedTotal} • ${progress.formattedSpeed} • ETA: ${progress.formattedEta}"

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading: $title")
            .setContentText(speedAndEta)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress.percentage, progress.totalBytes <= 0)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", cancelPendingIntent)
            .setOngoing(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun updateNotificationStatus(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, 0, true)
            .setOngoing(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun updateNotificationComplete(title: String, success: Boolean, message: String) {
        val icon = if (success) android.R.drawable.stat_sys_download_done else android.R.drawable.stat_notify_error
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(if (success) "Download Complete" else "Download Failed")
            .setContentText("$title: $message")
            .setSmallIcon(icon)
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager?.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseWakeLock()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
