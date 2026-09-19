package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.AdditionalFeaturesStep
import com.example.ui.components.DeviceSelectionStep
import com.example.ui.components.OsSelectionStep
import com.example.ui.components.RecoverySelectionStep
import com.example.ui.components.ReviewAndBuildStep
import com.example.ui.components.VersionSelectionStep
import com.example.ui.components.WizardStepperBar
import com.example.ui.theme.HoloTheme

@Composable
fun BuilderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = HoloTheme.colors

    val buildConfig by viewModel.buildConfig.collectAsStateWithLifecycle()
    val currentStep by viewModel.currentWizardStep.collectAsStateWithLifecycle()
    val batteryState by viewModel.batteryState.collectAsStateWithLifecycle()

    val isBuilding by viewModel.isServiceBuilding.collectAsStateWithLifecycle()
    val progress by viewModel.serviceProgress.collectAsStateWithLifecycle()
    val statusText by viewModel.serviceStatusText.collectAsStateWithLifecycle()
    val terminalLogs by viewModel.serviceTerminalLogs.collectAsStateWithLifecycle()
    val buildResult by viewModel.serviceBuildResult.collectAsStateWithLifecycle()
    val buildError by viewModel.serviceBuildError.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("builder_screen")
    ) {
        // Multi-Step Interactive Holo Stepper
        WizardStepperBar(
            currentStep = currentStep,
            onStepSelected = { stepIndex ->
                viewModel.setWizardStep(stepIndex)
            }
        )

        // Animated Step Transition Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "WizardStepTransition"
            ) { step ->
                when (step) {
                    0 -> {
                        // Step 1: Target Device Hardware (First Screen)
                        DeviceSelectionStep(
                            selectedDevice = buildConfig.device,
                            selectedTargetWifi = buildConfig.targetWifiSsid,
                            onSelectDevice = { dev -> viewModel.selectDevice(dev) },
                            onSelectTargetWifi = { ssid -> viewModel.selectTargetWifi(ssid) },
                            onNext = { viewModel.nextWizardStep() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    1 -> {
                        // Step 2: Choose Recovery
                        RecoverySelectionStep(
                            selectedRecovery = buildConfig.recovery,
                            onSelectRecovery = { rec -> viewModel.selectRecovery(rec) },
                            onPrevious = { viewModel.previousWizardStep() },
                            onNext = { viewModel.nextWizardStep() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    2 -> {
                        // Step 3: Select Operating System
                        OsSelectionStep(
                            selectedOs = buildConfig.os,
                            onSelectOs = { os -> viewModel.selectOs(os) },
                            onPrevious = { viewModel.previousWizardStep() },
                            onNext = { viewModel.nextWizardStep() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    3 -> {
                        // Step 4: Choose OS Version & Architecture Downgrade
                        VersionSelectionStep(
                            selectedOs = buildConfig.os,
                            selectedVersion = buildConfig.selectedVersion,
                            onSelectVersion = { ver -> viewModel.selectVersion(ver) },
                            selectedArchDowngrade = buildConfig.archDowngrade,
                            onSelectArchDowngrade = { arch -> viewModel.selectArchDowngrade(arch) },
                            onPrevious = { viewModel.previousWizardStep() },
                            onNext = { viewModel.nextWizardStep() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    4 -> {
                        // Step 5: Additional Features & ZIP Mods
                        AdditionalFeaturesStep(
                            features = buildConfig.additionalFeatures,
                            onSetMagiskOption = { opt -> viewModel.setMagiskOption(opt) },
                            onUpdateMagiskCustomization = { mCfg -> viewModel.setMagiskCustomization(mCfg) },
                            onToggleDebloater = { en -> viewModel.toggleDebloaterScript(en) },
                            onToggleCustomKernel = { en -> viewModel.toggleCustomKernelFlasher(en) },
                            onToggleSafeStrap = { en -> viewModel.toggleSafeStrap(en) },
                            onToggleBusybox = { en -> viewModel.toggleBusybox(en) },
                            onToggleMicroG = { en -> viewModel.toggleMicroG(en) },
                            onToggleSignatureSpoofing = { en -> viewModel.toggleSignatureSpoofing(en) },
                            onToggleBatterySaver = { en -> viewModel.toggleBatterySaver(en) },
                            onToggleViper4Android = { en -> viewModel.toggleViper4Android(en) },
                            onToggleZramSwap = { en -> viewModel.toggleZramSwapOptimizer(en) },
                            onToggleAdAway = { en -> viewModel.toggleAdAwayHosts(en) },
                            onToggleFDroidExt = { en -> viewModel.toggleFDroidPrivilegedExt(en) },
                            onToggleThermalMitigation = { en -> viewModel.toggleThermalMitigation(en) },
                            onToggleForceDexPreopt = { en -> viewModel.toggleForceDexPreopt(en) },
                            onToggleAppOpsPrivacy = { en -> viewModel.toggleAppOpsPrivacy(en) },
                            onToggleDalvikHeap = { en -> viewModel.toggleDalvikHeapOptimizer(en) },
                            onApplyHeaviestPreset = { viewModel.configureHeaviestPreset() },
                            onPrevious = { viewModel.previousWizardStep() },
                            onNext = { viewModel.nextWizardStep() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    5 -> {
                        // Step 6: Review & Flashable Repackaging Service
                        ReviewAndBuildStep(
                            config = buildConfig,
                            isBuilding = isBuilding,
                            progress = progress,
                            statusText = statusText,
                            terminalLogs = terminalLogs,
                            buildResult = buildResult,
                            buildError = buildError,
                            isWifiRequirementMet = batteryState.isWifiRequirementMet,
                            isWifiSimulated = batteryState.isWifiSimulated,
                            onToggleWifiSimulation = { en -> viewModel.toggleWifiSimulation(en) },
                            onApplyHeaviestPreset = { viewModel.configureHeaviestPreset() },
                            onStartBuild = { viewModel.startFirmwareBuildService() },
                            onCancelBuild = { viewModel.cancelFirmwareBuildService() },
                            onResetBuild = { viewModel.resetBuildState() },
                            onShareFile = { path ->
                                val shareIntent = viewModel.createShareIntent(path)
                                if (shareIntent != null) {
                                    context.startActivity(shareIntent)
                                } else {
                                    Toast.makeText(context, "File not found on device", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onPrevious = { viewModel.previousWizardStep() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        DeviceSelectionStep(
                            selectedDevice = buildConfig.device,
                            selectedTargetWifi = buildConfig.targetWifiSsid,
                            onSelectDevice = { dev -> viewModel.selectDevice(dev) },
                            onSelectTargetWifi = { ssid -> viewModel.selectTargetWifi(ssid) },
                            onNext = { viewModel.nextWizardStep() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
