package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloOrangeLight
import com.example.ui.theme.HoloRedLight
import com.example.ui.theme.HoloTheme

/**
 * Authentic Android 4.0 Ice Cream Sandwich (ICS) Section Header.
 * Features all-caps Holo Blue (#33B5E5) typography followed by a 1.5dp Holo Blue horizontal divider line.
 */
@Composable
fun IcsSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val colors = HoloTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = HoloBlueLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = title.uppercase(),
                    color = HoloBlueLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            trailingContent?.invoke()
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Iconic ICS Holo Blue horizontal underline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(HoloBlueLight)
        )
    }
}

/**
 * Authentic Android 4.0 Ice Cream Sandwich (ICS) Holo Switch.
 * Rectangular sliding toggle widget with "ON" in Holo Blue and "OFF" in muted gray.
 */
@Composable
fun IcsSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "ics_switch"
) {
    val colors = HoloTheme.colors
    val switchWidth = 72.dp
    val switchHeight = 28.dp
    val thumbWidth = 36.dp

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 36.dp else 0.dp,
        animationSpec = tween(durationMillis = 180),
        label = "IcsSwitchAnimation"
    )

    Box(
        modifier = modifier
            .size(width = switchWidth, height = switchHeight)
            .clip(RoundedCornerShape(2.dp))
            .background(if (colors.isDark) Color(0xFF141414) else Color(0xFFDCDCDC))
            .border(
                width = 1.dp,
                color = if (checked) HoloBlueLight else (if (colors.isDark) Color(0xFF333333) else Color(0xFFB0B0B0)),
                shape = RoundedCornerShape(2.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onCheckedChange(!checked)
            }
            .testTag(testTag)
    ) {
        // Labels ON and OFF
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ON",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (checked) HoloBlueLight else Color(0xFF555555)
            )
            Text(
                text = "OFF",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (!checked) (if (colors.isDark) Color(0xFFCCCCCC) else Color(0xFF444444)) else Color(0xFF555555)
            )
        }

        // Sliding Rectangular Thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(width = thumbWidth, height = switchHeight)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    if (checked) {
                        if (colors.isDark) Color(0xFF0F3B4C) else Color(0xFFCAEAF6)
                    } else {
                        if (colors.isDark) Color(0xFF2C2C2C) else Color(0xFFFFFFFF)
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (checked) HoloBlueLight else (if (colors.isDark) Color(0xFF444444) else Color(0xFFAAAAAA)),
                    shape = RoundedCornerShape(2.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Iconic 3 vertical grip grooves on ICS switch thumb
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(10.dp)
                            .background(if (checked) HoloBlueLight else colors.textSecondary)
                    )
                }
            }
        }
    }
}

/**
 * Authentic ICS Button with flat surface, 2dp corners, and Holo Blue bottom accent.
 */
@Composable
fun IcsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = "ics_button"
) {
    val colors = HoloTheme.colors

    if (isPrimary) {
        Column(modifier = modifier) {
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (colors.isDark) Color(0xFF1E2832) else Color(0xFFD6EDF8),
                    contentColor = if (colors.isDark) Color.White else Color(0xFF003F54),
                    disabledContainerColor = if (colors.isDark) Color(0xFF181818) else Color(0xFFE0E0E0),
                    disabledContentColor = Color(0xFF777777)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (enabled) HoloBlueDark else colors.border,
                        RoundedCornerShape(2.dp)
                    )
                    .testTag(testTag)
            ) {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = text.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            // Glowing Holo Blue bottom stripe
            if (enabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(HoloBlueLight)
                )
            }
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colors.textPrimary,
                disabledContentColor = colors.textSecondary
            ),
            modifier = modifier
                .border(1.dp, colors.border, RoundedCornerShape(2.dp))
                .testTag(testTag)
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Authentic ICS Holo Wi-Fi Requirement Banner.
 * Clearly informs the user when Wi-Fi is connected vs missing,
 * and provides a 1-tap testing bypass simulation switch.
 */
@Composable
fun IcsWifiRequirementBanner(
    isWifiConnected: Boolean,
    isWifiSimulated: Boolean,
    onToggleSimulation: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HoloTheme.colors
    val isRequirementMet = isWifiConnected || isWifiSimulated

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(
                if (isRequirementMet) {
                    if (colors.isDark) Color(0xFF0C1D24) else Color(0xFFE2F4FA)
                } else {
                    if (colors.isDark) Color(0xFF241505) else Color(0xFFFFF3E0)
                }
            )
            .border(
                width = 1.dp,
                color = if (isRequirementMet) HoloBlueLight else HoloOrangeLight,
                shape = RoundedCornerShape(2.dp)
            )
            .padding(10.dp)
            .testTag("ics_wifi_banner")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isRequirementMet) Icons.Default.SignalWifi4Bar else Icons.Default.SignalWifiOff,
                        contentDescription = "Wi-Fi Status",
                        tint = if (isRequirementMet) HoloBlueLight else HoloOrangeLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isRequirementMet) {
                                if (isWifiSimulated) "WI-FI ACTIVE (SIMULATION / BYPASS MODE)" else "WI-FI CONNECTED"
                            } else {
                                "WI-FI CONNECTION REQUIRED"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isRequirementMet) HoloBlueLight else HoloOrangeLight,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isRequirementMet) {
                                "High-speed ROM and toolchain packaging enabled"
                            } else {
                                "Firmware packaging requires Wi-Fi to prevent data charges"
                            },
                            fontSize = 10.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                // Inline switch for test simulation
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Simulate Wi-Fi",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    IcsSwitch(
                        checked = isWifiSimulated,
                        onCheckedChange = onToggleSimulation,
                        testTag = "wifi_simulation_switch"
                    )
                }
            }

            // Bottom accent rule in Holo Blue
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(if (isRequirementMet) HoloBlueLight else HoloOrangeLight)
            )
        }
    }
}
