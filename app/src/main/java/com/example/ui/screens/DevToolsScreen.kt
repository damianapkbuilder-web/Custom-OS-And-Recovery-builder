package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.MainViewModel
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloOrangeLight
import com.example.ui.theme.HoloRedLight
import com.example.ui.theme.HoloTheme

@Composable
fun DevToolsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = HoloTheme.colors

    var simulatedCrashCounter by remember { mutableIntStateOf(0) }
    var triggerMissingTextureGlitch by remember { mutableStateOf(false) }
    var simulateNullPointerBug by remember { mutableStateOf(false) }
    var simulateMemoryLeakLoop by remember { mutableStateOf(false) }
    var corruptedBufferLog by remember { mutableStateOf("") }
    var showRawMemoryDump by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Public Demo Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = if (colors.isDark) Color(0xFF1E1405) else Color(0xFFFFF3CD)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HoloOrangeLight, RoundedCornerShape(4.dp))
                    .testTag("dev_tools_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DeveloperMode,
                        contentDescription = "Dev Mode",
                        tint = HoloOrangeLight,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PUBLIC DEMO v0.8.0 DEV LAB",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HoloOrangeLight
                        )
                        Text(
                            text = "Experimental build with developer debugging hooks, intentional chaos simulation, missing texture fallbacks, and crash triggers.",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        // Section 1: Intentional Error & Crash Triggers
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (colors.isDark) Color(0xFF333333) else Color(0xFFCCCCCC), RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Bugs",
                            tint = HoloRedLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "INTENTIONAL BUG & CRASH INJECTORS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HoloRedLight
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Trigger real runtime crashes, artificial NullPointerExceptions, and buffer overflows on purpose:",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                simulatedCrashCounter++
                                if (simulatedCrashCounter >= 2) {
                                    throw RuntimeException("PUBLIC DEMO v0.8.0 INTENTIONAL CRASH: NullPointerException inside Stage3_PipelineExtractor.so at offset 0xDEADBEEF")
                                } else {
                                    Toast.makeText(context, "Warning: Press once more to cause fatal crash!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloRedLight,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_trigger_crash")
                        ) {
                            Text("FORCE CRASH (${simulatedCrashCounter}/2)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                simulateNullPointerBug = !simulateNullPointerBug
                                corruptedBufferLog += "[NPE-SIM] SIGSEGV in libart.so: thread #14 faulted on 0x00000000\n"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (simulateNullPointerBug) HoloOrangeLight else Color(0xFF444444),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_toggle_npe")
                        ) {
                            Text(if (simulateNullPointerBug) "NPE FAULT: ON" else "INJECT NPE FAULT", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 2: Missing Textures & Missing Asset Glitch
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (colors.isDark) Color(0xFF333333) else Color(0xFFCCCCCC), RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ImageNotSupported,
                                contentDescription = "Texture Bug",
                                tint = Color(0xFFFF00FF),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MISSING TEXTURE CHECKERBOARD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF00FF)
                            )
                        }
                        Switch(
                            checked = triggerMissingTextureGlitch,
                            onCheckedChange = { triggerMissingTextureGlitch = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFF00FF),
                                checkedTrackColor = Color(0xFF4A004A)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Renders classic magenta/black missing texture patterns and broken sprite buffers.",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )

                    AnimatedVisibility(visible = triggerMissingTextureGlitch) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .border(2.dp, Color(0xFFFF00FF))
                                    .background(Color.Black)
                            ) {
                                Row(modifier = Modifier.fillMaxSize()) {
                                    for (i in 0..7) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            for (j in 0..3) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .weight(1f)
                                                        .background(
                                                            if ((i + j) % 2 == 0) Color(0xFFFF00FF) else Color.Black
                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text = "ERROR: TEXTURE_RES_NOT_FOUND (ERR_0x9948)",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .background(Color.Black.copy(alpha = 0.8f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Corrupted Memory & Buffer Dump
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (colors.isDark) Color(0xFF333333) else Color(0xFFCCCCCC), RoundedCornerShape(4.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "Memory",
                                tint = HoloBlueLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BUFFER OVERFLOW & CORRUPTED DUMPS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HoloBlueLight
                            )
                        }

                        Button(
                            onClick = {
                                corruptedBufferLog += "[FAULT:0x${(1000..9999).random()}] Stack frame corruption detected in payload_extractor.bin at memory addr 0x7F${(100000..999999).random()}\n"
                                showRawMemoryDump = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HoloBlueDark),
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text("CORRUPT DUMP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (corruptedBufferLog.isNotEmpty() || showRawMemoryDump) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0A0A0A))
                                .border(1.dp, HoloBlueLight)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = corruptedBufferLog.ifEmpty { "[DEMO v0.8.0] Memory register clean. Press Corrupt Dump to inject hardware fault." },
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = HoloGreenLight
                            )
                        }
                    }
                }
            }
        }

        // Section 4: APK Download & Build Package Info
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HoloGreenLight, RoundedCornerShape(4.dp))
                    .testTag("demo_apk_info_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "APK",
                            tint = HoloGreenLight,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PUBLIC DEMO APK READY (v0.8.0)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HoloGreenLight
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Build Output: /app/apk_files/recovery_rom_builder.apk\nPackage: com.aistudio.recoveryrom.bldxz\nVersion: 0.8.0 (Build 80)",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Direct Download link available in web server and local workspace files.",
                        fontSize = 11.sp,
                        color = HoloBlueLight
                    )
                }
            }
        }
    }
}
