package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiLock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceModel
import com.example.data.model.DeviceRepository
import com.example.data.model.WifiTargetNetwork
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloOrangeLight
import com.example.ui.theme.HoloTheme

@Composable
fun DeviceSelectionStep(
    selectedDevice: DeviceModel,
    selectedTargetWifi: String,
    onSelectDevice: (DeviceModel) -> Unit,
    onSelectTargetWifi: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HoloTheme.colors
    var searchQuery by remember { mutableStateOf("") }
    var selectedBrandFilter by remember { mutableStateOf("All") }
    var showWifiSelectorForDevice by remember { mutableStateOf<String?>(selectedDevice.id) }

    val brands = listOf("All", "Galaxy S3", "Samsung", "Google", "HTC", "Motorola", "OnePlus", "Generic")

    val filteredDevices = remember(searchQuery, selectedBrandFilter) {
        DeviceRepository.devices.filter { device ->
            val matchesQuery = searchQuery.isBlank() ||
                    device.name.contains(searchQuery, ignoreCase = true) ||
                    device.codename.contains(searchQuery, ignoreCase = true) ||
                    device.modelNumber.contains(searchQuery, ignoreCase = true) ||
                    device.description.contains(searchQuery, ignoreCase = true) ||
                    device.socDetails.contains(searchQuery, ignoreCase = true)

            val matchesBrand = when (selectedBrandFilter) {
                "All" -> true
                "Galaxy S3" -> device.id.startsWith("galaxy_s3") || device.name.contains("Galaxy S III", ignoreCase = true)
                else -> device.manufacturer.contains(selectedBrandFilter, ignoreCase = true) ||
                        device.name.contains(selectedBrandFilter, ignoreCase = true)
            }

            matchesQuery && matchesBrand
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("step_device_selection")
    ) {
        // Step Banner in ICS Holo style
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "STEP 1: TARGET DEVICE & WI-FI TARGET",
                        color = HoloBlueLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "${filteredDevices.size} HARDWARE TARGETS",
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Select your target device model number, chipset variant, and the Wi-Fi AP network the device will connect to for repo downloads & flashing.",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Search bar
        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Search devices or models (e.g. GT-I9300, SGH-I747, d2vzw, Exynos, S3)...", fontSize = 11.sp)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = HoloBlueLight, modifier = Modifier.size(18.dp))
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("device_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HoloBlueLight,
                    unfocusedBorderColor = colors.border,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface
                ),
                shape = RoundedCornerShape(2.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Brand & Family filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            brands.forEach { brand ->
                val isSelected = selectedBrandFilter == brand
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isSelected) {
                                if (colors.isDark) Color(0xFF142433) else Color(0xFFD6EDF8)
                            } else colors.surface
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) HoloBlueLight else colors.border,
                            shape = RoundedCornerShape(2.dp)
                        )
                        .clickable { selectedBrandFilter = brand }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = brand.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) HoloBlueLight else colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Devices List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredDevices, key = { it.id }) { device ->
                val isSelected = device.id == selectedDevice.id
                val cardBg = if (isSelected) {
                    if (colors.isDark) Color(0xFF122330) else Color(0xFFE5F3FA)
                } else colors.surface

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                        .background(cardBg)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) HoloBlueLight else colors.border,
                            shape = RoundedCornerShape(2.dp)
                        )
                        .clickable {
                            onSelectDevice(device)
                            showWifiSelectorForDevice = device.id
                        }
                        .padding(10.dp)
                        .testTag("device_item_${device.id}")
                ) {
                    Column {
                        // Title & Hardware Badges
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = if (isSelected) "Selected" else "Not selected",
                                    tint = if (isSelected) HoloBlueLight else colors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = device.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) HoloBlueLight else colors.textPrimary
                                        )
                                        if (device.modelNumber.isNotBlank()) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(if (colors.isDark) Color(0xFF1F2F3D) else Color(0xFFCCE4F5))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = device.modelNumber,
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    color = HoloBlueLight
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "Model: ${device.modelNumber.ifBlank { "N/A" }} | Codename: ${device.codename} | ${device.manufacturer}",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            // Instruction Set Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (colors.isDark) Color(0xFF1B2B38) else Color(0xFFD6EDF8))
                                    .border(1.dp, HoloBlueDark, RoundedCornerShape(2.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = device.nativeArch,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = HoloBlueLight
                                )
                            }
                        }

                        // SoC & Hardware Chip Info
                        if (device.socDetails.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SOC: ",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HoloBlueLight
                                )
                                Text(
                                    text = device.socDetails,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = colors.textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Description
                        Text(
                            text = device.description,
                            fontSize = 11.sp,
                            color = colors.textSecondary,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Bottom badges (Android version, locked BL)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Version badge
                            Box(
                                modifier = Modifier
                                    .background(colors.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = device.androidVersionBadge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                            }

                            if (device.requiresSafeStrap) {
                                Box(
                                    modifier = Modifier
                                        .background(if (colors.isDark) Color(0xFF331D00) else Color(0xFFFFF3E0))
                                        .border(1.dp, HoloOrangeLight, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "SafeStrap Required",
                                            tint = HoloOrangeLight,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Locked Bootloader / SafeStrap",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HoloOrangeLight
                                        )
                                    }
                                }
                            }
                        }

                        // =============================================================
                        // WI-FI CONNECTION TARGET CONFIGURATION (FOR SELECTED DEVICE)
                        // =============================================================
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (colors.isDark) Color(0xFF0D1C28) else Color(0xFFDEEFF9))
                                    .border(1.dp, HoloBlueLight.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
                                    .padding(8.dp)
                                    .testTag("device_wifi_config_box")
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Wifi,
                                                contentDescription = "Wi-Fi Config",
                                                tint = HoloBlueLight,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "TARGET WI-FI CONNECTION NETWORK",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = HoloBlueLight,
                                                letterSpacing = 0.5.sp
                                            )
                                        }

                                        Text(
                                            text = "Chip: ${device.wifiHardwareChip.take(22)}...",
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = colors.textSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Specify which Wi-Fi network this ${device.modelNumber.ifBlank { device.name }} will connect to for repo sync and build verification:",
                                        fontSize = 10.sp,
                                        color = colors.textSecondary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // List of selectable Wi-Fi networks for this model
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        device.availableWifiNetworks.forEach { network ->
                                            val isWifiSelected = selectedTargetWifi == network.ssid
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(
                                                        if (isWifiSelected) {
                                                            if (colors.isDark) Color(0xFF143048) else Color(0xFFBCE0F7)
                                                        } else {
                                                            colors.surface
                                                        }
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isWifiSelected) HoloBlueLight else colors.border,
                                                        RoundedCornerShape(2.dp)
                                                    )
                                                    .clickable { onSelectTargetWifi(network.ssid) }
                                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                                    .testTag("wifi_network_${network.ssid}")
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isWifiSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                                            contentDescription = "Radio",
                                                            tint = if (isWifiSelected) HoloBlueLight else colors.textSecondary,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Column {
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Text(
                                                                    text = network.ssid,
                                                                    fontSize = 11.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = if (isWifiSelected) HoloBlueLight else colors.textPrimary
                                                                )
                                                                Spacer(modifier = Modifier.width(6.dp))
                                                                Text(
                                                                    text = network.frequencyBand,
                                                                    fontSize = 9.sp,
                                                                    fontFamily = FontFamily.Monospace,
                                                                    color = colors.textSecondary
                                                                )
                                                            }
                                                            Text(
                                                                text = "${network.securityType} • ${network.note}",
                                                                fontSize = 9.sp,
                                                                color = colors.textSecondary
                                                            )
                                                        }
                                                    }

                                                    if (isWifiSelected) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(2.dp))
                                                                .background(if (colors.isDark) Color(0xFF0F2615) else Color(0xFFE8F5E9))
                                                                .border(1.dp, HoloGreenLight, RoundedCornerShape(2.dp))
                                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                                        ) {
                                                            Text(
                                                                text = "CONNECTED",
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = HoloGreenLight
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .border(1.dp, colors.border)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TARGET: ${selectedDevice.name.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HoloBlueLight
                    )
                    Text(
                        text = "Model: ${selectedDevice.modelNumber.ifBlank { "N/A" }} | Wi-Fi: $selectedTargetWifi",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textSecondary
                    )
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.testTag("step_device_next_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloBlueDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text(
                        text = "NEXT: RECOVERY ->",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
