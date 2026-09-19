package com.example.ui

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.engine.BatteryEfficiencyManager
import com.example.data.engine.BuildResult
import com.example.data.local.AppDatabase
import com.example.data.local.BuildEntity
import com.example.data.model.ArchDowngradeOption
import com.example.data.model.BuildConfiguration
import com.example.data.model.DeviceModel
import com.example.data.model.DeviceRepository
import com.example.data.model.MagiskCustomization
import com.example.data.model.MagiskOption
import com.example.data.model.MagiskPatchMode
import com.example.data.model.MagiskRootAccess
import com.example.data.model.OsModel
import com.example.data.model.OsRepository
import com.example.data.model.RecoveryModel
import com.example.data.model.RecoveryRepository
import com.example.data.model.TargetType
import com.example.data.repository.BuildRepository
import com.example.data.service.FirmwareBuildService
import com.example.ui.theme.HoloThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

sealed interface BuildState {
    data object Idle : BuildState
    data class Building(val progress: Float, val statusText: String) : BuildState
    data class Completed(val result: BuildResult) : BuildState
    data class Failed(val error: String) : BuildState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = BuildRepository(database.buildDao())
    private val batteryManager = BatteryEfficiencyManager(application)

    val batteryState = batteryManager.state

    // App Holo Theme (Holo Dark, Holo Light, or Light with Dark Action Bar)
    private val _appThemeMode = MutableStateFlow(HoloThemeMode.HOLO_DARK)
    val appThemeMode: StateFlow<HoloThemeMode> = _appThemeMode.asStateFlow()

    // Multi-Step Wizard: 0=Recovery, 1=OS, 2=Version, 3=Features, 4=Review & Build
    private val _currentWizardStep = MutableStateFlow(0)
    val currentWizardStep: StateFlow<Int> = _currentWizardStep.asStateFlow()

    val savedBuilds: StateFlow<List<BuildEntity>> = repository.allBuilds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingSyncQueue = repository.pendingSyncQueue
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingSyncCount: StateFlow<Int> = pendingSyncQueue
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _buildConfig = MutableStateFlow(
        BuildConfiguration(
            recovery = RecoveryRepository.recoveries[2], // TWRP default
            os = OsRepository.operatingSystems[1], // LineageOS default
            selectedVersion = "LineageOS 21 (U)"
        )
    )
    val buildConfig: StateFlow<BuildConfiguration> = _buildConfig.asStateFlow()

    // Service-connected live state
    val isServiceBuilding = FirmwareBuildService.isBuilding
    val serviceProgress = FirmwareBuildService.progress
    val serviceStatusText = FirmwareBuildService.statusText
    val serviceTerminalLogs = FirmwareBuildService.terminalLogs
    val serviceBuildResult = FirmwareBuildService.buildResult
    val serviceBuildError = FirmwareBuildService.buildError

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        // Collect service results into user message
        viewModelScope.launch {
            FirmwareBuildService.buildResult.collect { res ->
                if (res != null) {
                    _userMessage.value = "Build complete! ${res.zipFileName} ready for download."
                }
            }
        }
        viewModelScope.launch {
            FirmwareBuildService.buildError.collect { err ->
                if (err != null) {
                    _userMessage.value = "Error: $err"
                }
            }
        }
    }

    fun setAppTheme(mode: HoloThemeMode) {
        _appThemeMode.value = mode
    }

    fun setWizardStep(step: Int) {
        if (step in 0..5) {
            _currentWizardStep.value = step
        }
    }

    fun nextWizardStep() {
        if (_currentWizardStep.value < 5) {
            _currentWizardStep.value += 1
        }
    }

    fun previousWizardStep() {
        if (_currentWizardStep.value > 0) {
            _currentWizardStep.value -= 1
        }
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun setTargetType(type: TargetType) {
        _buildConfig.value = _buildConfig.value.copy(targetType = type)
    }

    fun selectDevice(device: DeviceModel) {
        val current = _buildConfig.value
        val safeStrapNeeded = device.requiresSafeStrap || current.additionalFeatures.safeStrapHijack
        _buildConfig.value = current.copy(
            device = device,
            targetWifiSsid = device.defaultTargetWifiSsid,
            additionalFeatures = current.additionalFeatures.copy(
                safeStrapHijack = safeStrapNeeded
            )
        )
    }

    fun selectTargetWifi(ssid: String) {
        _buildConfig.value = _buildConfig.value.copy(targetWifiSsid = ssid)
    }

    // Step 1: Select Recovery
    fun selectRecovery(recovery: RecoveryModel) {
        val current = _buildConfig.value
        val safeStrapMod = recovery.supportsLockedBootloader || current.additionalFeatures.safeStrapHijack
        _buildConfig.value = current.copy(
            recovery = recovery,
            additionalFeatures = current.additionalFeatures.copy(
                safeStrapHijack = safeStrapMod
            )
        )
    }

    // Step 2: Select OS
    fun selectOs(os: OsModel) {
        _buildConfig.value = _buildConfig.value.copy(
            os = os,
            selectedVersion = os.defaultVersions.firstOrNull() ?: "v1.0"
        )
    }

    // Step 3: Select Version
    fun selectVersion(version: String) {
        _buildConfig.value = _buildConfig.value.copy(selectedVersion = version)
    }

    fun selectArchDowngrade(option: ArchDowngradeOption) {
        _buildConfig.value = _buildConfig.value.copy(archDowngrade = option)
    }

    // Step 4: Additional Features
    fun setGuiStyle(style: HoloThemeMode) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(guiStyle = style)
        )
    }

    fun setMagiskOption(option: MagiskOption) {
        val currentFeatures = _buildConfig.value.additionalFeatures
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = currentFeatures.copy(
                magiskOption = option,
                magiskCustomization = currentFeatures.magiskCustomization.copy(option = option)
            )
        )
    }

    fun setMagiskCustomization(customization: MagiskCustomization) {
        val currentFeatures = _buildConfig.value.additionalFeatures
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = currentFeatures.copy(
                magiskOption = customization.option,
                magiskCustomization = customization
            )
        )
    }

    fun toggleWifiSimulation(enabled: Boolean) {
        batteryManager.toggleWifiSimulation(enabled)
    }

    fun toggleDebloaterScript(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(debloaterScript = enabled)
        )
    }

    fun toggleCustomKernelFlasher(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(customKernelFlasher = enabled)
        )
    }

    fun toggleSafeStrap(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(safeStrapHijack = enabled)
        )
    }

    fun toggleBusybox(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(busyboxInjection = enabled)
        )
    }

    fun toggleMicroG(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(microGInjection = enabled)
        )
    }

    fun toggleSignatureSpoofing(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(signatureSpoofing = enabled)
        )
    }

    fun toggleBatterySaver(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(batterySaverProfile = enabled)
        )
    }

    fun toggleViper4Android(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(viper4AndroidFx = enabled)
        )
    }

    fun toggleZramSwapOptimizer(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(zramSwapOptimizer = enabled)
        )
    }

    fun toggleAdAwayHosts(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(adawaySystemlessHosts = enabled)
        )
    }

    fun toggleFDroidPrivilegedExt(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(fDroidPrivilegedExt = enabled)
        )
    }

    fun toggleThermalMitigation(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(thermalMitigationTweak = enabled)
        )
    }

    fun toggleForceDexPreopt(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(forceDexPreopt = enabled)
        )
    }

    fun toggleAppOpsPrivacy(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(appOpsPrivacyManager = enabled)
        )
    }

    fun toggleDalvikHeapOptimizer(enabled: Boolean) {
        _buildConfig.value = _buildConfig.value.copy(
            additionalFeatures = _buildConfig.value.additionalFeatures.copy(dalvikHeapOptimizer = enabled)
        )
    }

    // Step 6: Start Background Service
    fun startFirmwareBuildService() {
        if (isServiceBuilding.value) return

        if (!batteryManager.state.value.isWifiRequirementMet) {
            _userMessage.value = "Wi-Fi connection is required to package firmware and download OS branches. Please connect to Wi-Fi or enable simulation mode."
            return
        }

        if (!batteryManager.isSafeForIntensiveWork()) {
            _userMessage.value = "Low battery warning: Device below 15% without charger. Proceeding in battery efficiency mode."
        }

        FirmwareBuildService.clearResult()
        FirmwareBuildService.startService(
            context = getApplication(),
            config = _buildConfig.value
        )
    }

    fun cancelFirmwareBuildService() {
        FirmwareBuildService.cancelService(getApplication())
    }

    fun resetBuildState() {
        FirmwareBuildService.clearResult()
    }

    /**
     * Heavy Preset: Selects OrangeFox, Xiaomi HyperOS Latest, All-in-One Multi-Root, and all additional feature mods
     */
    fun configureHeaviestPreset() {
        val orangeFox = RecoveryRepository.recoveries.find { it.id == "orangefox" } ?: RecoveryRepository.recoveries[0]
        val hyperOs = OsRepository.operatingSystems.find { it.id == "miui_eu" } ?: OsRepository.operatingSystems[0]
        val latestVersion = hyperOs.defaultVersions.lastOrNull() ?: "Xiaomi HyperOS 1.0 EU"

        val heavyFeatures = _buildConfig.value.additionalFeatures.copy(
            magiskOption = MagiskOption.ALL_ROOTS,
            magiskCustomization = MagiskCustomization(
                option = MagiskOption.ALL_ROOTS,
                zygiskEnabled = true,
                denyListEnabled = true,
                systemlessHosts = true,
                patchMode = MagiskPatchMode.FLASHABLE_ZIP,
                rootAccess = MagiskRootAccess.APPS_AND_ADB,
                randomizeStubPkg = true
            ),
            debloaterScript = true,
            customKernelFlasher = true,
            safeStrapHijack = true,
            busyboxInjection = true,
            microGInjection = true,
            signatureSpoofing = true,
            batterySaverProfile = true,
            viper4AndroidFx = true,
            zramSwapOptimizer = true,
            adawaySystemlessHosts = true,
            fDroidPrivilegedExt = true,
            thermalMitigationTweak = true,
            forceDexPreopt = true,
            appOpsPrivacyManager = true,
            dalvikHeapOptimizer = true
        )

        _buildConfig.value = _buildConfig.value.copy(
            recovery = orangeFox,
            os = hyperOs,
            selectedVersion = latestVersion,
            additionalFeatures = heavyFeatures
        )

        _userMessage.value = "Heaviest Ultimate ZIP preset loaded! (OrangeFox + HyperOS + All-in-One Root + All Mods)"
    }

    fun deleteBuild(build: BuildEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(build.zipFilePath)
            if (file.exists()) {
                file.delete()
            }
            repository.deleteBuild(build)
        }
    }

    fun syncPendingItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val pending = pendingSyncQueue.value
            if (pending.isEmpty()) {
                _userMessage.value = "All data is already synchronized."
                return@launch
            }
            for (item in pending) {
                repository.updateSyncItem(
                    item.copy(
                        isSynced = true,
                        lastAttempt = System.currentTimeMillis()
                    )
                )
            }
            _userMessage.value = "Synchronized ${pending.size} offline items successfully!"
        }
    }

    fun createShareIntent(filePath: String): Intent? {
        val file = File(filePath)
        if (!file.exists()) return null
        val context = getApplication<Application>()
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Flashable ZIP: ${file.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
