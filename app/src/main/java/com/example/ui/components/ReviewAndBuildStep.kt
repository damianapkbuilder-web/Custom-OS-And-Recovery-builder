package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.BuildResult
import com.example.data.model.BuildConfiguration
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloRedLight
import com.example.ui.theme.HoloTerminalBg
import com.example.ui.theme.HoloTerminalCyan
import com.example.ui.theme.HoloTerminalGreen
import com.example.ui.theme.HoloTheme

@Composable
fun ReviewAndBuildStep(
    config: BuildConfiguration,
    isBuilding: Boolean,
    progress: Float,
    statusText: String,
    terminalLogs: List<String>,
    buildResult: BuildResult?,
    buildError: String?,
    isWifiRequirementMet: Boolean = true,
    isWifiSimulated: Boolean = false,
    onToggleWifiSimulation: (Boolean) -> Unit = {},
    onStartBuild: () -> Unit,
    onCancelBuild: () -> Unit,
    onResetBuild: () -> Unit,
    onShareFile: (String) -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HoloTheme.colors
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LaunchedEffect(terminalLogs.size) {
        if (terminalLogs.isNotEmpty()) {
            listState.animateScrollToItem(terminalLogs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("step_review_build")
    ) {
        // Step Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "STEP 6: REVIEW & FLASHABLE SERVICE",
                    color = HoloBlueLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Verify selections, network prerequisites, and launch background download, extraction, modification, and repackaging service.",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Configuration Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(3.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "CONFIGURATION SUMMARY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HoloBlueLight,
                        letterSpacing = 0.5.sp
                    )

                    SummaryItem(label = "Target Device:", value = "${config.device.name} [${config.device.codename}]")
                    if (config.device.modelNumber.isNotBlank()) {
                        SummaryItem(label = "Device Model No:", value = config.device.modelNumber)
                    }
                    SummaryItem(label = "Target Wi-Fi AP:", value = "${config.targetWifiSsid} (${config.device.wifiHardwareChip.take(20)}...)")
                    SummaryItem(label = "Target Recovery:", value = "${config.recovery?.name ?: "TWRP"}")
                    SummaryItem(label = "Selected OS:", value = "${config.os?.name ?: "Custom OS"} (${config.selectedVersion})")
                    SummaryItem(label = "Architecture:", value = config.archDowngrade.label)
                    SummaryItem(label = "GUI Theme:", value = "${config.additionalFeatures.guiStyle.title} (${config.additionalFeatures.guiStyle.systemThemeName})")
                    SummaryItem(label = "Magisk Root:", value = config.additionalFeatures.magiskOption.label)

                    if (config.additionalFeatures.magiskOption != com.example.data.model.MagiskOption.NONE) {
                        val mCfg = config.additionalFeatures.magiskCustomization
                        SummaryItem(
                            label = "Magisk Modules:",
                            value = "Zygisk: ${if (mCfg.zygiskEnabled) "ON" else "OFF"} | DenyList: ${if (mCfg.denyListEnabled) "ON" else "OFF"} | Hosts: ${if (mCfg.systemlessHosts) "ON" else "OFF"}"
                        )
                        SummaryItem(label = "Root Patch Mode:", value = mCfg.patchMode.label)
                    }

                    val activeMods = mutableListOf<String>()
                    if (config.additionalFeatures.viper4AndroidFx) activeMods.add("ViPER4Android FX")
                    if (config.additionalFeatures.zramSwapOptimizer) activeMods.add("ZRAM 2GB Swap")
                    if (config.additionalFeatures.adawaySystemlessHosts) activeMods.add("AdAway Hosts")
                    if (config.additionalFeatures.fDroidPrivilegedExt) activeMods.add("F-Droid PrivExt")
                    if (config.additionalFeatures.appOpsPrivacyManager) activeMods.add("AppOps Privacy")
                    if (config.additionalFeatures.dalvikHeapOptimizer) activeMods.add("Dalvik Heap 512M")
                    if (config.additionalFeatures.debloaterScript) activeMods.add("Debloater")
                    if (config.additionalFeatures.customKernelFlasher) activeMods.add("Custom Kernel")
                    if (config.additionalFeatures.busyboxInjection) activeMods.add("BusyBox")
                    if (config.additionalFeatures.microGInjection) activeMods.add("microG")
                    if (config.additionalFeatures.signatureSpoofing) activeMods.add("SigSpoof")
                    if (config.additionalFeatures.batterySaverProfile) activeMods.add("Battery Saver")
                    if (config.additionalFeatures.thermalMitigationTweak) activeMods.add("Fast Charge")
                    if (config.additionalFeatures.forceDexPreopt) activeMods.add("DexPreopt ART")
                    if (config.additionalFeatures.safeStrapHijack) activeMods.add("SafeStrap")

                    SummaryItem(label = "Active ZIP Mods:", value = if (activeMods.isEmpty()) "None" else activeMods.joinToString(", "))
                    SummaryItem(
                        label = "Wi-Fi Network Status:",
                        value = if (isWifiRequirementMet) {
                            if (isWifiSimulated) "ACTIVE (Simulation Bypass)" else "CONNECTED (High-Speed)"
                        } else {
                            "DISCONNECTED (Wi-Fi Required)"
                        }
                    )
                }
            }

            // Real-time Service Status & Progress Bar
            if (isBuilding || buildResult != null || buildError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(3.dp))
                        .background(colors.surface)
                        .border(
                            1.dp,
                            when {
                                buildError != null -> HoloRedLight
                                buildResult != null -> HoloGreenLight
                                else -> HoloBlueLight
                            },
                            RoundedCornerShape(3.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    buildError != null -> HoloRedLight
                                    buildResult != null -> HoloGreenLight
                                    else -> colors.textPrimary
                                }
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HoloBlueLight
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .testTag("build_service_progress_bar"),
                            color = when {
                                buildError != null -> HoloRedLight
                                buildResult != null -> HoloGreenLight
                                else -> HoloBlueLight
                            },
                            trackColor = colors.surfaceVariant
                        )
                    }
                }
            }

            // Terminal Logs Window
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(3.dp))
                    .background(HoloTerminalBg)
                    .border(1.dp, if (isBuilding) HoloBlueLight else Color(0xFF222222), RoundedCornerShape(3.dp))
                    .padding(8.dp)
            ) {
                if (terminalLogs.isEmpty()) {
                    Text(
                        text = "> Firmware Build Service is ready.\n> Click 'START FLASHABLE PACKAGING SERVICE' below to commence background download, extraction, modification, and repacking.",
                        color = Color(0xFF888888),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.testTag("service_terminal_logs")
                    ) {
                        items(terminalLogs) { line ->
                            val textColor = when {
                                line.startsWith("[ERROR") -> HoloRedLight
                                line.startsWith("[NET") -> HoloTerminalCyan
                                line.startsWith("[EXTRACT") -> Color(0xFFCCCCCC)
                                line.startsWith("[MODIFY") || line.startsWith("[PATCH") -> HoloTerminalGreen
                                line.startsWith("[REPACK") || line.startsWith("[CHECKSUM") -> Color(0xFFFFBB33)
                                else -> Color(0xFFEEEEEE)
                            }
                            Text(
                                text = line,
                                color = textColor,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // Result Action Card (When Completed)
            if (buildResult != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (colors.isDark) Color(0xFF0F2615) else Color(0xFFE8F5E9))
                        .border(1.dp, HoloGreenLight, RoundedCornerShape(3.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = HoloGreenLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FLASHABLE ZIP READY IN DOWNLOADS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = HoloGreenLight
                            )
                        }

                        Text(
                            text = "Saved to: /storage/emulated/0/Download/${buildResult.zipFileName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Size: ${buildResult.fileSizeBytes / (1024 * 1024)} MB | MD5: ${buildResult.md5Checksum} | Ready for TWRP / recovery flash",
                            fontSize = 10.sp,
                            color = colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onShareFile(buildResult.zipFile.absolutePath) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("download_flashable_zip_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HoloBlueDark,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(2.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download / Share", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onResetBuild,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reset_build_btn"),
                                shape = RoundedCornerShape(2.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "New", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("New Build", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Error Display Card
            if (buildError != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (colors.isDark) Color(0xFF2E0F0F) else Color(0xFFFFEBEE))
                        .border(1.dp, HoloRedLight, RoundedCornerShape(3.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = "Error", tint = HoloRedLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BUILD SERVICE ERROR", color = HoloRedLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = buildError, color = colors.textPrimary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Wi-Fi Requirement Notice (if not met)
        if (!isWifiRequirementMet) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (colors.isDark) Color(0xFF2E1900) else Color(0xFFFFF3E0))
                    .border(1.dp, com.example.ui.theme.HoloOrangeLight, RoundedCornerShape(3.dp))
                    .padding(10.dp)
                    .testTag("wifi_required_warning_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.Error,
                                contentDescription = "Wi-Fi Required",
                                tint = com.example.ui.theme.HoloOrangeLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WI-FI CONNECTION REQUIRED",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = com.example.ui.theme.HoloOrangeLight
                            )
                        }

                        // One-tap testing bypass button
                        OutlinedButton(
                            onClick = { onToggleWifiSimulation(true) },
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.testTag("enable_wifi_sim_btn")
                        ) {
                            Text("Bypass / Simulate Wi-Fi", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "Packaging and downloading ROM repositories requires an active unmetered Wi-Fi connection. Enable Wi-Fi or toggle Wi-Fi simulation for testing.",
                        fontSize = 10.sp,
                        color = colors.textPrimary
                    )
                }
            }
        }

        // Bottom Action Controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .border(1.dp, colors.border)
                .padding(12.dp)
        ) {
            if (isBuilding) {
                Button(
                    onClick = onCancelBuild,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cancel_service_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloRedLight,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = "Cancel", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CANCEL PACKAGING SERVICE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onPrevious,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("step_review_back_btn"),
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.textPrimary
                        )
                    ) {
                        Text("<- FEATURES", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onStartBuild,
                        modifier = Modifier
                            .weight(2f)
                            .testTag("start_firmware_service_btn"),
                        enabled = isWifiRequirementMet,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloBlueDark,
                            contentColor = Color.White,
                            disabledContainerColor = if (colors.isDark) Color(0xFF222222) else Color(0xFFDDDDDD),
                            disabledContentColor = Color(0xFF777777)
                        ),
                        shape = RoundedCornerShape(3.dp)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = "Build", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isWifiRequirementMet) "START PACKAGING SERVICE" else "WI-FI REQUIRED TO START",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    val colors = HoloTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = colors.textSecondary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 11.sp,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}
