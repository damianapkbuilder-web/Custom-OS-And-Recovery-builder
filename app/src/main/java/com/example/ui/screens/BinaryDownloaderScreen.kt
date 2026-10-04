package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.downloader.BinaryDownloadViewModel
import com.example.data.downloader.ChecksumAlgorithm
import com.example.data.downloader.DestinationType
import com.example.data.downloader.DownloadRequest
import com.example.data.downloader.DownloadStatus
import com.example.data.downloader.PresetBinary
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenDark
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloOrangeDark
import com.example.ui.theme.HoloOrangeLight
import com.example.ui.theme.HoloRedDark
import com.example.ui.theme.HoloRedLight
import com.example.ui.theme.HoloTheme
import java.io.File

@Composable
fun BinaryDownloaderScreen(
    downloadViewModel: BinaryDownloadViewModel = viewModel()
) {
    val context = LocalContext.current
    val downloadState by downloadViewModel.downloadState.collectAsStateWithLifecycle()
    val terminalLogs by downloadViewModel.terminalLogs.collectAsStateWithLifecycle()
    val manualResult by downloadViewModel.manualVerificationResult.collectAsStateWithLifecycle()
    val isVerifyingManual by downloadViewModel.isVerifyingManual.collectAsStateWithLifecycle()
    val manualProgress by downloadViewModel.manualVerifyProgress.collectAsStateWithLifecycle()
    val colors = HoloTheme.colors

    var selectedDestination by remember { mutableStateOf(DestinationType.APP_EXTERNAL_STORAGE) }
    var customUrl by remember { mutableStateOf("") }
    var customFileName by remember { mutableStateOf("recovery-custom.img") }
    var customSha256 by remember { mutableStateOf("") }
    var customMd5 by remember { mutableStateOf("") }

    // Manual hash verification input
    var manualFilePath by remember { mutableStateOf("") }
    var manualExpectedHash by remember { mutableStateOf("") }
    var selectedAlgorithm by remember { mutableStateOf(ChecksumAlgorithm.SHA256) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Stream Dashboard
        item {
            ActiveDownloadCard(
                state = downloadState,
                onCancel = { downloadViewModel.cancelDownload(context) },
                onClear = { downloadViewModel.clearDownloadState() }
            )
        }

        // Storage Scope Selector
        item {
            StorageDestinationCard(
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestination = it }
            )
        }

        // Curated ROM & Recovery Catalog
        item {
            Text(
                text = "VERIFIED ROM & RECOVERY CATALOG",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = HoloBlueLight,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(downloadViewModel.presetCatalog) { preset ->
            PresetBinaryCard(
                preset = preset,
                destinationType = selectedDestination,
                isDownloading = downloadState.status == DownloadStatus.DOWNLOADING || downloadState.status == DownloadStatus.CONNECTING,
                onDownloadClick = { req ->
                    downloadViewModel.startDownload(context, req)
                }
            )
        }

        // Custom Stream Downloader
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (colors.isDark) Color(0xFF141414) else Color(0xFFF0F0F0)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.accent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "CUSTOM STREAM URL DOWNLOADER",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent,
                        fontFamily = FontFamily.Monospace
                    )

                    OutlinedTextField(
                        value = customUrl,
                        onValueChange = { customUrl = it },
                        label = { Text("Direct Binary URL (HTTP / HTTPS)") },
                        placeholder = { Text("https://example.com/rom.zip") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_custom_download_url"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HoloBlueLight,
                            focusedLabelColor = HoloBlueLight
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customFileName,
                            onValueChange = { customFileName = it },
                            label = { Text("Target File Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HoloBlueLight,
                                focusedLabelColor = HoloBlueLight
                            )
                        )
                    }

                    OutlinedTextField(
                        value = customSha256,
                        onValueChange = { customSha256 = it },
                        label = { Text("Expected SHA-256 Checksum (Recommended)") },
                        placeholder = { Text("e.g. b94d27b9934d3e08a52e52d7da7dab...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HoloGreenLight,
                            focusedLabelColor = HoloGreenLight
                        )
                    )

                    OutlinedTextField(
                        value = customMd5,
                        onValueChange = { customMd5 = it },
                        label = { Text("Expected MD5 Checksum (Optional)") },
                        placeholder = { Text("e.g. 2c5a089d3807aa3c6e9a01f7823e4ab8") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HoloOrangeLight,
                            focusedLabelColor = HoloOrangeLight
                        )
                    )

                    Button(
                        onClick = {
                            if (customUrl.isNotBlank()) {
                                val req = DownloadRequest(
                                    url = customUrl.trim(),
                                    fileName = customFileName.trim(),
                                    destinationType = selectedDestination,
                                    expectedSha256 = customSha256.takeIf { it.isNotBlank() },
                                    expectedMd5 = customMd5.takeIf { it.isNotBlank() },
                                    category = "Custom Stream",
                                    deviceTarget = "User Specified"
                                )
                                downloadViewModel.startDownload(context, req)
                            }
                        },
                        enabled = customUrl.isNotBlank() && downloadState.status != DownloadStatus.DOWNLOADING,
                        colors = ButtonDefaults.buttonColors(containerColor = HoloBlueDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_start_custom_download")
                    ) {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = "Start Stream")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Foreground Stream & Verify")
                    }
                }
            }
        }

        // Standalone Checksum Integrity Auditor
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (colors.isDark) Color(0xFF111111) else Color(0xFFEBEBEB)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HoloGreenDark.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = "Security", tint = HoloGreenLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "IMAGE CHECKSUM AUDITOR",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HoloGreenLight,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Compute cryptographic checksums on downloaded images or flashable zips before flashing in TWRP/Fastboot.",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )

                    OutlinedTextField(
                        value = manualFilePath,
                        onValueChange = { manualFilePath = it },
                        label = { Text("Absolute File Path / Saved File") },
                        placeholder = { Text(downloadState.savedFile?.absolutePath ?: "/storage/emulated/0/Download/...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HoloGreenLight,
                            focusedLabelColor = HoloGreenLight
                        )
                    )

                    OutlinedTextField(
                        value = manualExpectedHash,
                        onValueChange = { manualExpectedHash = it },
                        label = { Text("Expected Checksum Hash") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HoloGreenLight,
                            focusedLabelColor = HoloGreenLight
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedAlgorithm == ChecksumAlgorithm.SHA256,
                                onClick = { selectedAlgorithm = ChecksumAlgorithm.SHA256 },
                                colors = RadioButtonDefaults.colors(selectedColor = HoloGreenLight)
                            )
                            Text("SHA-256", fontSize = 12.sp, color = colors.textPrimary)

                            Spacer(modifier = Modifier.width(12.dp))

                            RadioButton(
                                selected = selectedAlgorithm == ChecksumAlgorithm.MD5,
                                onClick = { selectedAlgorithm = ChecksumAlgorithm.MD5 },
                                colors = RadioButtonDefaults.colors(selectedColor = HoloGreenLight)
                            )
                            Text("MD5", fontSize = 12.sp, color = colors.textPrimary)
                        }

                        Button(
                            onClick = {
                                val targetPath = manualFilePath.ifBlank { downloadState.savedFile?.absolutePath }
                                if (!targetPath.isNullOrBlank()) {
                                    val f = File(targetPath)
                                    if (f.exists()) {
                                        downloadViewModel.verifyLocalFile(f, manualExpectedHash, selectedAlgorithm)
                                    }
                                }
                            },
                            enabled = !isVerifyingManual && (manualFilePath.isNotBlank() || downloadState.savedFile != null),
                            colors = ButtonDefaults.buttonColors(containerColor = HoloGreenDark),
                            modifier = Modifier.testTag("btn_audit_checksum")
                        ) {
                            Text("Verify Hash")
                        }
                    }

                    if (isVerifyingManual) {
                        LinearProgressIndicator(
                            progress = { manualProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = HoloGreenLight
                        )
                    }

                    manualResult?.let { res ->
                        ChecksumResultBadge(result = res)
                    }
                }
            }
        }

        // Streaming Logs Terminal
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A0A)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "LIVE STREAM & RECOVERY LOGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF888888),
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (terminalLogs.isEmpty()) {
                        Text(
                            text = "[SYSTEM] Ready. Select a package above to begin streaming with resume and SHA-256 verification.",
                            fontSize = 11.sp,
                            color = Color(0xFF666666),
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        terminalLogs.takeLast(12).forEach { line ->
                            val textColor = when {
                                line.contains("[ERROR", ignoreCase = true) -> HoloRedLight
                                line.contains("[WARNING", ignoreCase = true) -> HoloOrangeLight
                                line.contains("[CHECKSUM", ignoreCase = true) -> HoloGreenLight
                                else -> Color(0xFFCCCCCC)
                            }
                            Text(
                                text = line,
                                fontSize = 11.sp,
                                color = textColor,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveDownloadCard(
    state: com.example.data.downloader.DownloadUiSnapshot,
    onCancel: () -> Unit,
    onClear: () -> Unit
) {
    val colors = HoloTheme.colors

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (state.status) {
                DownloadStatus.DOWNLOADING, DownloadStatus.CONNECTING -> if (colors.isDark) Color(0xFF0F1B2B) else Color(0xFFE3F2FD)
                DownloadStatus.COMPLETED -> if (colors.isDark) Color(0xFF0E2413) else Color(0xFFE8F5E9)
                DownloadStatus.FAILED -> if (colors.isDark) Color(0xFF2A0F11) else Color(0xFFFFEBEE)
                else -> if (colors.isDark) Color(0xFF141414) else Color(0xFFF2F2F2)
            }
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                when (state.status) {
                    DownloadStatus.DOWNLOADING, DownloadStatus.CONNECTING -> HoloBlueLight
                    DownloadStatus.COMPLETED -> HoloGreenLight
                    DownloadStatus.FAILED -> HoloRedLight
                    else -> colors.border
                },
                RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (state.status) {
                            DownloadStatus.DOWNLOADING -> Icons.Default.CloudDownload
                            DownloadStatus.COMPLETED -> Icons.Default.CheckCircle
                            DownloadStatus.FAILED -> Icons.Default.Error
                            else -> Icons.Default.Download
                        },
                        contentDescription = "Status Icon",
                        tint = when (state.status) {
                            DownloadStatus.DOWNLOADING -> HoloBlueLight
                            DownloadStatus.COMPLETED -> HoloGreenLight
                            DownloadStatus.FAILED -> HoloRedLight
                            else -> colors.accent
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = state.activeRequest?.fileName ?: "No Active Download",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                StatusBadge(status = state.status)
            }

            Text(
                text = state.statusMessage,
                fontSize = 12.sp,
                color = colors.textSecondary
            )

            // Progress Bar
            if (state.status == DownloadStatus.DOWNLOADING || state.status == DownloadStatus.CONNECTING || state.status == DownloadStatus.VERIFYING_CHECKSUM) {
                LinearProgressIndicator(
                    progress = { state.progress.progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = HoloBlueLight,
                    trackColor = Color(0xFF333333)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${state.progress.percentage}% (${state.progress.formattedDownloaded} / ${state.progress.formattedTotal})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = "Speed", tint = HoloBlueLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = state.progress.formattedSpeed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HoloBlueLight,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(imageVector = Icons.Default.Timer, contentDescription = "ETA", tint = HoloOrangeLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ETA: ${state.progress.formattedEta}",
                            fontSize = 12.sp,
                            color = HoloOrangeLight,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (state.status == DownloadStatus.DOWNLOADING || state.status == DownloadStatus.CONNECTING) {
                    OutlinedButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HoloRedLight)
                    ) {
                        Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancel")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel Stream")
                    }
                } else if (state.status == DownloadStatus.COMPLETED || state.status == DownloadStatus.FAILED || state.status == DownloadStatus.CANCELLED) {
                    OutlinedButton(
                        onClick = onClear,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.accent)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Clear")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: DownloadStatus) {
    val (bgColor, textColor, text) = when (status) {
        DownloadStatus.IDLE -> Triple(Color(0xFF333333), Color(0xFFBBBBBB), "IDLE")
        DownloadStatus.CONNECTING -> Triple(HoloOrangeDark, Color.White, "CONNECTING")
        DownloadStatus.DOWNLOADING -> Triple(HoloBlueDark, Color.White, "STREAMING")
        DownloadStatus.PAUSED -> Triple(HoloOrangeDark, Color.White, "PAUSED")
        DownloadStatus.VERIFYING_CHECKSUM -> Triple(Color(0xFF673AB7), Color.White, "VERIFYING")
        DownloadStatus.COMPLETED -> Triple(HoloGreenDark, Color.White, "VERIFIED")
        DownloadStatus.FAILED -> Triple(HoloRedDark, Color.White, "FAILED")
        DownloadStatus.CANCELLED -> Triple(Color(0xFF555555), Color.White, "CANCELLED")
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun StorageDestinationCard(
    selectedDestination: DestinationType,
    onDestinationSelected: (DestinationType) -> Unit
) {
    val colors = HoloTheme.colors

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (colors.isDark) Color(0xFF141414) else Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Folder, contentDescription = "Folder", tint = HoloBlueLight)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "STORAGE DESTINATION (SCOPED STORAGE)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = HoloBlueLight,
                    fontFamily = FontFamily.Monospace
                )
            }

            DestinationType.values().forEach { dest ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedDestination == dest,
                        onClick = { onDestinationSelected(dest) },
                        colors = RadioButtonDefaults.colors(selectedColor = HoloBlueLight)
                    )
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text(
                            text = dest.displayName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = dest.description,
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PresetBinaryCard(
    preset: PresetBinary,
    destinationType: DestinationType,
    isDownloading: Boolean,
    onDownloadClick: (DownloadRequest) -> Unit
) {
    val colors = HoloTheme.colors

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (colors.isDark) Color(0xFF161616) else Color(0xFFF8F8F8)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = preset.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "${preset.category} • ${preset.deviceTarget} • ~${preset.fileSizeEstimate}",
                        fontSize = 11.sp,
                        color = HoloBlueLight,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {
                        val req = DownloadRequest(
                            url = preset.url,
                            fileName = preset.fileName,
                            destinationType = destinationType,
                            expectedSha256 = preset.expectedSha256,
                            expectedMd5 = preset.expectedMd5,
                            category = preset.category,
                            deviceTarget = preset.deviceTarget,
                            description = preset.description
                        )
                        onDownloadClick(req)
                    },
                    enabled = !isDownloading,
                    colors = ButtonDefaults.buttonColors(containerColor = HoloBlueDark)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Download")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download")
                }
            }

            Text(
                text = preset.description,
                fontSize = 12.sp,
                color = colors.textSecondary
            )

            Text(
                text = "SHA-256: ${preset.expectedSha256.take(16)}...${preset.expectedSha256.takeLast(8)}",
                fontSize = 10.sp,
                color = HoloGreenLight,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun ChecksumResultBadge(result: com.example.data.downloader.ChecksumVerificationResult) {
    val bgColor = if (result.isMatch) Color(0xFF1B5E20) else Color(0xFFB71C1C)
    val icon = if (result.isMatch) Icons.Default.Verified else Icons.Default.Error

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = "Verification Result", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (result.isMatch) "INTEGRITY CERTIFIED: HASH MATCHED" else "INTEGRITY ALERT: HASH MISMATCH",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Calculated: ${result.calculatedHash}",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontFamily = FontFamily.Monospace
            )
            if (result.expectedHash.isNotBlank()) {
                Text(
                    text = "Expected:   ${result.expectedHash}",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = "Time: ${result.elapsedMs}ms • File: ${result.verifiedFile.name}",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
