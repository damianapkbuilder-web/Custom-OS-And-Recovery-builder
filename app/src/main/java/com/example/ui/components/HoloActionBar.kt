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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloTheme
import com.example.ui.theme.HoloThemeMode

@Composable
fun HoloActionBar(
    currentTheme: HoloThemeMode,
    onThemeSelected: (HoloThemeMode) -> Unit,
    isWifiConnected: Boolean = true,
    isWifiSimulated: Boolean = false,
    onToggleWifiSimulation: ((Boolean) -> Unit)? = null,
    onApplyHeaviestPreset: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showThemeMenu by remember { mutableStateOf(false) }
    val colors = HoloTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.actionBarBackground)
            .testTag("holo_action_bar")
    ) {
        // Row 1: Title and Theme dropdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Holo App branding icon + title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Classic Holo App Icon Box
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF000000))
                        .border(1.dp, HoloBlueLight, RoundedCornerShape(3.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Studio Icon",
                        tint = HoloBlueLight,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Recovery & ROM Studio",
                        color = colors.actionBarText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "Flashable Packaging & Downgrade Engine",
                        color = if (colors.isDark) Color(0xFF9E9E9E) else Color(0xFF666666),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Theme selector action widget
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (colors.isDark) Color(0xFF1E1E1E) else Color(0xFFDDDDDD))
                        .border(1.dp, if (colors.isDark) Color(0xFF333333) else Color(0xFFCCCCCC), RoundedCornerShape(3.dp))
                        .clickable { showThemeMenu = true }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("theme_selector_dropdown_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = "Theme",
                        tint = HoloBlueLight,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentTheme.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.actionBarText
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        tint = colors.actionBarText,
                        modifier = Modifier.size(15.dp)
                    )
                }

                DropdownMenu(
                    expanded = showThemeMenu,
                    onDismissRequest = { showThemeMenu = false },
                    modifier = Modifier
                        .background(if (colors.isDark) Color(0xFF141414) else Color(0xFFFFFFFF))
                        .border(1.dp, HoloBlueLight)
                ) {
                    HoloThemeMode.entries.forEach { mode ->
                        val isSelected = mode == currentTheme
                        DropdownMenuItem(
                            text = {
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        text = mode.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) HoloBlueLight else if (colors.isDark) Color.White else Color.Black,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = mode.systemThemeName,
                                        fontSize = 10.sp,
                                        color = if (colors.isDark) Color(0xFF888888) else Color(0xFF777777)
                                    )
                                }
                            },
                            onClick = {
                                onThemeSelected(mode)
                                showThemeMenu = false
                            },
                            modifier = Modifier.testTag("theme_menu_item_${mode.name}")
                        )
                    }
                }
            }
        }

        // Row 2: Heaviest Preset and Wi-Fi status quick toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Heaviest ZIP Preset Quick Button
            if (onApplyHeaviestPreset != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFB71C1C))
                        .border(1.dp, Color(0xFFFF5252), RoundedCornerShape(3.dp))
                        .clickable { onApplyHeaviestPreset() }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("heaviest_zip_action_btn")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Heaviest ZIP",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "HEAVIEST ZIP PRESET",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Wi-Fi requirement status pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (colors.isDark) Color(0xFF1A1A1A) else Color(0xFFE8E8E8))
                    .border(
                        1.dp,
                        if (isWifiConnected) HoloBlueLight else com.example.ui.theme.HoloOrangeLight,
                        RoundedCornerShape(3.dp)
                    )
                    .clickable {
                        onToggleWifiSimulation?.invoke(!isWifiSimulated)
                    }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("action_bar_wifi_status")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isWifiConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = if (isWifiConnected) "Wi-Fi Connected" else "Wi-Fi Disconnected",
                        tint = if (isWifiConnected) HoloBlueLight else com.example.ui.theme.HoloOrangeLight,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isWifiConnected) (if (isWifiSimulated) "Wi-Fi (Simulated)" else "Wi-Fi Connected") else "No Wi-Fi",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isWifiConnected) HoloBlueLight else com.example.ui.theme.HoloOrangeLight
                    )
                }
            }
        }

        // Signature Holo Action Bar bottom border (vibrant 2dp Holo Blue strip)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(HoloBlueLight)
        )
    }
}
