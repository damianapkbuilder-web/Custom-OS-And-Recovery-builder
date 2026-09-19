package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloOrangeLight
import com.example.ui.theme.HoloRedLight
import com.example.ui.theme.HoloTheme

@Composable
fun BatterySyncScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val batteryState by viewModel.batteryState.collectAsStateWithLifecycle()
    val syncQueue by viewModel.pendingSyncQueue.collectAsStateWithLifecycle()
    val colors = HoloTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Screen Header
        Text(
            text = "BATTERY & OFFLINE DATA SYNC",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = HoloBlueLight,
            letterSpacing = 0.5.sp
        )

        // Battery Status Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(3.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(3.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                batteryState.isCharging -> Icons.Default.BatteryChargingFull
                                batteryState.batteryLevel <= 15 -> Icons.Default.BatteryAlert
                                else -> Icons.Default.BatteryFull
                            },
                            contentDescription = "Battery",
                            tint = when {
                                batteryState.isCharging -> HoloGreenLight
                                batteryState.batteryLevel <= 15 -> HoloRedLight
                                else -> HoloBlueLight
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Battery Level: ${batteryState.batteryLevel}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colors.textPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (batteryState.isCharging) {
                                    if (colors.isDark) Color(0xFF0F2615) else Color(0xFFE8F5E9)
                                } else {
                                    colors.surfaceVariant
                                }
                            )
                            .border(1.dp, if (batteryState.isCharging) HoloGreenLight else colors.border, RoundedCornerShape(2.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (batteryState.isCharging) "CHARGING" else "BATTERY POWER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (batteryState.isCharging) HoloGreenLight else colors.textSecondary
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { batteryState.batteryLevel / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = when {
                        batteryState.isCharging -> HoloGreenLight
                        batteryState.batteryLevel <= 15 -> HoloRedLight
                        else -> HoloBlueLight
                    },
                    trackColor = colors.surfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (batteryState.isCharging) "Power: External (Charging)" else "Power: Battery Discharging",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                    Text(
                        text = if (batteryState.isPowerSaveMode) "Power Saver: ACTIVE" else "Power Saver: NORMAL",
                        fontSize = 11.sp,
                        color = if (batteryState.isPowerSaveMode) HoloOrangeLight else colors.textSecondary
                    )
                }
            }
        }

        // Offline Network State Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(3.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(3.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (batteryState.isNetworkAvailable) Icons.Default.SignalWifi4Bar else Icons.Default.SignalWifiOff,
                        contentDescription = "Network",
                        tint = if (batteryState.isNetworkAvailable) HoloGreenLight else HoloOrangeLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (batteryState.isNetworkAvailable) "Network Connected" else "Operating Completely Offline",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (batteryState.isNetworkAvailable) {
                                if (batteryState.isMeteredConnection) "Connection: Metered (Bandwidth Optimized)" else "Connection: Unmetered Wi-Fi"
                            } else {
                                "Local packaging and database run 100% offline"
                            },
                            fontSize = 10.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        // Offline Sync Queue Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OFFLINE SYNC QUEUE (${syncQueue.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HoloBlueLight,
                letterSpacing = 0.5.sp
            )

            Button(
                onClick = { viewModel.syncPendingItems() },
                enabled = syncQueue.isNotEmpty(),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HoloBlueDark,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("sync_now_btn")
            ) {
                Icon(Icons.Default.Sync, contentDescription = "Sync", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sync Now", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (syncQueue.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(3.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Synced",
                        tint = HoloGreenLight,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "All Stored Firmware Records Synchronized",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "When new builds are generated offline, telemetry & metadata are queued and automatically synced when network returns.",
                        fontSize = 10.sp,
                        color = colors.textSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(syncQueue, key = { it.id }) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(3.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.border, RoundedCornerShape(3.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.syncType,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HoloOrangeLight
                                )
                                Text(
                                    text = "Pending",
                                    fontSize = 10.sp,
                                    color = HoloOrangeLight
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.payload,
                                fontSize = 10.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
