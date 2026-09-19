package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.BuildEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloRedLight
import com.example.ui.theme.HoloTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedBuildsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val builds by viewModel.savedBuilds.collectAsStateWithLifecycle()
    val colors = HoloTheme.colors

    var selectedBuildForLogs by remember { mutableStateOf<BuildEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "FLASHABLE PACKAGES",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HoloBlueLight,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${builds.size} flashable ZIP builds staged locally",
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(3.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "OFFLINE STORAGE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = HoloGreenLight
                )
            }
        }

        if (builds.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(3.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderZip,
                        contentDescription = "Empty",
                        tint = HoloBlueLight,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No Flashable ZIPs Built Yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Use the Builder tab to configure recovery, OS, GUI style, and Magisk mods to generate your first flashable ZIP.",
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(builds, key = { it.id }) { build ->
                    SavedBuildCard(
                        build = build,
                        onShare = {
                            val intent = viewModel.createShareIntent(build.zipFilePath)
                            if (intent != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "Archive file missing on disk", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onViewLogs = { selectedBuildForLogs = build },
                        onDelete = { viewModel.deleteBuild(build) }
                    )
                }
            }
        }

        // Log viewer modal/bottom sheet when item is selected
        selectedBuildForLogs?.let { build ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.surface)
                    .border(1.dp, HoloBlueLight, RoundedCornerShape(3.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BUILD LOG: ${build.zipFileName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HoloBlueLight
                        )
                        IconButton(
                            onClick = { selectedBuildForLogs = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text("X", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                        }
                    }

                    Text(
                        text = build.buildLog.takeLast(1200),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedBuildCard(
    build: BuildEntity,
    onShare: () -> Unit,
    onViewLogs: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = HoloTheme.colors
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US) }
    val dateStr = remember(build.timestamp) { dateFormat.format(Date(build.timestamp)) }
    val sizeMb = remember(build.fileSizeBytes) { build.fileSizeBytes / (1024 * 1024) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(3.dp))
            .padding(12.dp)
            .testTag("saved_build_card_${build.id}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = build.zipFileName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "${build.softwareName} ${build.softwareVersion} | ${build.deviceName}",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_build_${build.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = HoloRedLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = "Mods: ${build.featuresSummary}",
                fontSize = 10.sp,
                color = colors.textSecondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$sizeMb MB | MD5: ${build.md5Checksum.take(8)}... | $dateStr",
                    fontSize = 10.sp,
                    color = colors.textSecondary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onViewLogs,
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = HoloBlueLight
                        )
                    ) {
                        Text("Log", fontSize = 10.sp)
                    }

                    Button(
                        onClick = onShare,
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HoloBlueDark,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
