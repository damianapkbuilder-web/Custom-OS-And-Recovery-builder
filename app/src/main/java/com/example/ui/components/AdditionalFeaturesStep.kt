package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdditionalFeatures
import com.example.data.model.MagiskCustomization
import com.example.data.model.MagiskOption
import com.example.data.repository.DiscoveredOnlineItem
import com.example.data.repository.WebSearchDiscoveryEngine
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloOrangeLight
import com.example.ui.theme.HoloTheme
import com.example.ui.theme.HoloThemeMode
import kotlinx.coroutines.launch

@Composable
fun AdditionalFeaturesStep(
    features: AdditionalFeatures,
    onSetMagiskOption: (MagiskOption) -> Unit,
    onUpdateMagiskCustomization: (MagiskCustomization) -> Unit = {},
    onToggleDebloater: (Boolean) -> Unit,
    onToggleCustomKernel: (Boolean) -> Unit,
    onToggleSafeStrap: (Boolean) -> Unit,
    onToggleBusybox: (Boolean) -> Unit,
    onToggleMicroG: (Boolean) -> Unit,
    onToggleSignatureSpoofing: (Boolean) -> Unit,
    onToggleBatterySaver: (Boolean) -> Unit,
    onToggleViper4Android: (Boolean) -> Unit = {},
    onToggleZramSwap: (Boolean) -> Unit = {},
    onToggleAdAway: (Boolean) -> Unit = {},
    onToggleFDroidExt: (Boolean) -> Unit = {},
    onToggleThermalMitigation: (Boolean) -> Unit = {},
    onToggleForceDexPreopt: (Boolean) -> Unit = {},
    onToggleAppOpsPrivacy: (Boolean) -> Unit = {},
    onToggleDalvikHeap: (Boolean) -> Unit = {},
    onApplyHeaviestPreset: (() -> Unit)? = null,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HoloTheme.colors
    var searchQuery by remember { mutableStateOf("") }
    var isSearchingWeb by remember { mutableStateOf(false) }
    var webSearchResults by remember { mutableStateOf<List<DiscoveredOnlineItem>>(emptyList()) }
    var showWebResults by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun triggerWebSearch() {
        scope.launch {
            isSearchingWeb = true
            webSearchResults = WebSearchDiscoveryEngine.searchModsAndRootsOnline(searchQuery)
            isSearchingWeb = false
            showWebResults = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("step_additional_features")
    ) {
        // Step Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STEP 5: ADDITIONAL FEATURES & ZIP MODS",
                            color = HoloBlueLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Select root solutions (Kitsune, Magisk Alpha, KernelSU, APatch, SuperSU, or All-in-One), audio/system mods, and search web for latest releases.",
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        )
                    }

                    if (onApplyHeaviestPreset != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onApplyHeaviestPreset,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB71C1C),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier.testTag("features_heaviest_preset_btn")
                        ) {
                            Text("HEAVIEST PRESET", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Web Search Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mods_search_input"),
                        placeholder = {
                            Text("Search web for Kitsune, KernelSU, APatch, ViPER4Android...", fontSize = 10.sp, color = colors.textSecondary)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = HoloBlueLight,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HoloBlueLight,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        ),
                        shape = RoundedCornerShape(3.dp)
                    )

                    Button(
                        onClick = { triggerWebSearch() },
                        modifier = Modifier.testTag("mods_web_search_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloBlueDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(3.dp)
                    ) {
                        if (isSearchingWeb) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, contentDescription = "Search Web", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Web Search", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Web Search Discovery Results Card
        if (showWebResults && webSearchResults.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (colors.isDark) Color(0xFF0C202C) else Color(0xFFE1F5FE))
                    .border(1.dp, HoloBlueLight, RoundedCornerShape(3.dp))
                    .padding(10.dp)
                    .testTag("mods_web_results_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = HoloBlueLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DISCOVERED ONLINE MODS & ROOT RELEASES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HoloBlueLight)
                        }
                        Text(
                            text = "Dismiss",
                            fontSize = 10.sp,
                            color = HoloOrangeLight,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showWebResults = false }
                        )
                    }

                    webSearchResults.forEach { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(2.dp))
                                .background(colors.surface)
                                .border(1.dp, colors.border, RoundedCornerShape(2.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colors.textPrimary)
                                    Text("Repo: ${item.sourceRepo} • Tag: ${item.versionTag}", fontSize = 9.sp, color = colors.textSecondary)
                                    Text(item.description, fontSize = 9.sp, color = if (colors.isDark) Color(0xFF81C784) else Color(0xFF388E3C))
                                }
                                Button(
                                    onClick = {
                                        when (item.versionTag) {
                                            "all_roots" -> onSetMagiskOption(MagiskOption.ALL_ROOTS)
                                            "kitsune" -> onSetMagiskOption(MagiskOption.MAGISK_DELTA)
                                            "alpha" -> onSetMagiskOption(MagiskOption.MAGISK_ALPHA)
                                            "kernelsu" -> onSetMagiskOption(MagiskOption.KERNEL_SU)
                                            "apatch" -> onSetMagiskOption(MagiskOption.APATCH)
                                            "supersu" -> onSetMagiskOption(MagiskOption.SUPERSU)
                                            "v4a_fx" -> onToggleViper4Android(true)
                                            else -> onSetMagiskOption(MagiskOption.MAGISK_V27_0)
                                        }
                                        showWebResults = false
                                    },
                                    shape = RoundedCornerShape(2.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HoloBlueDark, contentColor = Color.White),
                                    modifier = Modifier.testTag("apply_web_mod_${item.versionTag}")
                                ) {
                                    Text("Apply", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // -------------------------------------------------------------
            // SECTION 1: ROOT & MULTI-ROOT SUITE SELECTION
            // -------------------------------------------------------------
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Root Options",
                        tint = HoloBlueLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "1. ROOT INTEGRATION (KITSUNE, ALPHA, KERNELSU, APATCH, SUPERSU, ALL-IN-ONE)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HoloBlueLight,
                        letterSpacing = 0.5.sp
                    )
                }

                // All Root Options
                MagiskOption.entries.forEach { option ->
                    val isSelected = features.magiskOption == option
                    val isAllRoots = option == MagiskOption.ALL_ROOTS
                    val cardBg = if (isSelected) {
                        if (isAllRoots) (if (colors.isDark) Color(0xFF1E3A2B) else Color(0xFFE8F5E9))
                        else (if (colors.isDark) Color(0xFF142433) else Color(0xFFE3F2FD))
                    } else colors.surface

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(3.dp))
                            .background(cardBg)
                            .border(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) (if (isAllRoots) HoloGreenLight else HoloBlueLight) else colors.border,
                                RoundedCornerShape(3.dp)
                            )
                            .clickable { onSetMagiskOption(option) }
                            .padding(10.dp)
                            .testTag("magisk_option_${option.versionTag}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (isSelected) "Selected" else "Unselected",
                                tint = if (isSelected) (if (isAllRoots) HoloGreenLight else HoloBlueLight) else colors.textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = option.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected && isAllRoots) HoloGreenLight else colors.textPrimary
                                    )
                                    if (isAllRoots) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(HoloGreenLight)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "ALL-IN-ONE BUNDLE",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = option.description,
                                    fontSize = 10.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 2: ADDITIONAL SYSTEM FEATURES & ZIP MODS
            // -------------------------------------------------------------
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = "ZIP Mods",
                        tint = HoloBlueLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "2. ADDITIONAL SYSTEM FEATURES & ZIP MODS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HoloBlueLight,
                        letterSpacing = 0.5.sp
                    )
                }

                // Feature Items
                FeatureToggleRow(
                    title = "ViPER4Android FX Audio DSP Suite",
                    description = "Injects system-wide high-definition sound clarity, dynamic bass, and headphone convolver processing.",
                    checked = features.viper4AndroidFx,
                    onCheckedChange = onToggleViper4Android,
                    testTag = "toggle_viper4android"
                )

                FeatureToggleRow(
                    title = "ZRAM & Swap RAM Compression Optimizer",
                    description = "Allocates 2048MB compressed ZRAM swap device with LZ4 algorithm for smoother multitasking.",
                    checked = features.zramSwapOptimizer,
                    onCheckedChange = onToggleZramSwap,
                    testTag = "toggle_zram_swap"
                )

                FeatureToggleRow(
                    title = "AdAway Systemless Unified Hosts",
                    description = "Injects systemless ad, tracker, and malware blocking hosts file into /system/etc/hosts.",
                    checked = features.adawaySystemlessHosts,
                    onCheckedChange = onToggleAdAway,
                    testTag = "toggle_adaway_hosts"
                )

                FeatureToggleRow(
                    title = "F-Droid Privileged Extension",
                    description = "Enables silent background package installation and updates directly through F-Droid.",
                    checked = features.fDroidPrivilegedExt,
                    onCheckedChange = onToggleFDroidExt,
                    testTag = "toggle_fdroid_ext"
                )

                FeatureToggleRow(
                    title = "Dalvik / ART VM Heap Optimizer",
                    description = "Configures 512MB heap size and low-memory killer boundaries for maximum app responsiveness.",
                    checked = features.dalvikHeapOptimizer,
                    onCheckedChange = onToggleDalvikHeap,
                    testTag = "toggle_dalvik_heap"
                )

                FeatureToggleRow(
                    title = "AppOps Privacy Hardening",
                    description = "Restricts background clipboard access, sensors, and camera isolation across all third-party apps.",
                    checked = features.appOpsPrivacyManager,
                    onCheckedChange = onToggleAppOpsPrivacy,
                    testTag = "toggle_appops_privacy"
                )

                FeatureToggleRow(
                    title = "Universal Carrier & Telemetry Debloater",
                    description = "Automated Edify script to strip carrier spyware, OEM bloatware, and invasive telemetry apps.",
                    checked = features.debloaterScript,
                    onCheckedChange = onToggleDebloater,
                    testTag = "toggle_debloater"
                )

                FeatureToggleRow(
                    title = "Custom Kernel & Governor Tuner",
                    description = "Injects tuned CPU schedutil/interactive governors and Adreno GPU frequency scaling parameters.",
                    checked = features.customKernelFlasher,
                    onCheckedChange = onToggleCustomKernel,
                    testTag = "toggle_custom_kernel"
                )

                FeatureToggleRow(
                    title = "BusyBox Unix Multi-Call Suite (v1.36.1)",
                    description = "Provides complete POSIX coreutils in /system/xbin for terminal administration and shell scripts.",
                    checked = features.busyboxInjection,
                    onCheckedChange = onToggleBusybox,
                    testTag = "toggle_busybox"
                )

                FeatureToggleRow(
                    title = "microG Open-Source GmsCore Suite",
                    description = "Replaces Google Play Services with lightweight open-source microG services.",
                    checked = features.microGInjection,
                    onCheckedChange = onToggleMicroG,
                    testTag = "toggle_microg"
                )

                FeatureToggleRow(
                    title = "Framework Signature Spoofing Patch",
                    description = "Patches services.jar to allow microG fake signature verification.",
                    checked = features.signatureSpoofing,
                    onCheckedChange = onToggleSignatureSpoofing,
                    testTag = "toggle_sigspoof"
                )

                FeatureToggleRow(
                    title = "Aggressive Doze & Battery Optimization",
                    description = "Applies Wi-Fi scan interval throttling, sensor sleep delays, and sleep-mode build properties.",
                    checked = features.batterySaverProfile,
                    onCheckedChange = onToggleBatterySaver,
                    testTag = "toggle_battery_saver"
                )

                FeatureToggleRow(
                    title = "Thermal Mitigation & Fast Charge Unlock",
                    description = "Relaxes thermal throttling tables to allow maximum charging currents during heavy loads.",
                    checked = features.thermalMitigationTweak,
                    onCheckedChange = onToggleThermalMitigation,
                    testTag = "toggle_thermal_mitigation"
                )

                FeatureToggleRow(
                    title = "Ahead-Of-Time DexPreopt ART Compilation",
                    description = "Forces full speed-profile ahead-of-time compilation of system bytecode for zero stutter.",
                    checked = features.forceDexPreopt,
                    onCheckedChange = onToggleForceDexPreopt,
                    testTag = "toggle_force_dexpreopt"
                )

                FeatureToggleRow(
                    title = "SafeStrap 2nd-Init Hijack",
                    description = "For locked bootloader devices (e.g. Motorola OMAP, Verizon Knox). Hijacks init via 2nd-init ramdisk.",
                    checked = features.safeStrapHijack,
                    onCheckedChange = onToggleSafeStrap,
                    testTag = "toggle_safestrap"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // Bottom Navigation Buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier.testTag("additional_features_prev_btn")
                ) {
                    Text("Previous", fontSize = 12.sp, color = colors.textPrimary)
                }

                Button(
                    onClick = onNext,
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloBlueDark,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("additional_features_next_btn")
                ) {
                    Text("Next: Review & Build", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FeatureToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val colors = HoloTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(3.dp))
            .padding(10.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = HoloBlueDark,
                    uncheckedThumbColor = colors.textSecondary,
                    uncheckedTrackColor = colors.surfaceVariant
                )
            )
        }
    }
}
