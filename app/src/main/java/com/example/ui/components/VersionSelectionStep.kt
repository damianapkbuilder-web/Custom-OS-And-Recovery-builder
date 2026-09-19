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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArchDowngradeOption
import com.example.data.model.OsModel
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloOrangeLight
import com.example.ui.theme.HoloTheme

import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import com.example.data.repository.DiscoveredOnlineItem
import com.example.data.repository.WebSearchDiscoveryEngine
import kotlinx.coroutines.launch

@Composable
fun VersionSelectionStep(
    selectedOs: OsModel?,
    selectedVersion: String,
    onSelectVersion: (String) -> Unit,
    selectedArchDowngrade: ArchDowngradeOption,
    onSelectArchDowngrade: (ArchDowngradeOption) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HoloTheme.colors
    val osName = selectedOs?.name ?: "Custom OS"
    val defaultVersions = selectedOs?.defaultVersions ?: listOf("v1.0 Release", "v2.0 Beta")
    var customVersionInput by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchingWeb by remember { mutableStateOf(false) }
    var webSearchResults by remember { mutableStateOf<List<DiscoveredOnlineItem>>(emptyList()) }
    var showWebResults by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun triggerWebSearch() {
        scope.launch {
            isSearchingWeb = true
            webSearchResults = WebSearchDiscoveryEngine.searchOsVersionsOnline(osName, searchQuery)
            isSearchingWeb = false
            showWebResults = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("step_version_selection")
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
                    text = "STEP 4: CHOOSE OS VERSION",
                    color = HoloBlueLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Select release build version, LineageOS 22/23/24, or search online release tags.",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Web search bar
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
                            .testTag("version_search_input"),
                        placeholder = {
                            Text("Search git tags / LineageOS 24, 23, 22...", fontSize = 11.sp, color = colors.textSecondary)
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
                        modifier = Modifier.testTag("version_web_search_btn"),
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
                    .testTag("version_web_results_card")
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
                            Text("DISCOVERED ONLINE RELEASE BRANCHES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HoloBlueLight)
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
                                    Text("Branch: ${item.channel} • Tag: ${item.versionTag}", fontSize = 9.sp, color = colors.textSecondary)
                                    Text(item.description, fontSize = 9.sp, color = if (colors.isDark) Color(0xFF81C784) else Color(0xFF388E3C))
                                }
                                Button(
                                    onClick = {
                                        onSelectVersion(item.versionTag)
                                        showWebResults = false
                                    },
                                    shape = RoundedCornerShape(2.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HoloBlueDark, contentColor = Color.White),
                                    modifier = Modifier.testTag("apply_web_version_${item.versionTag}")
                                ) {
                                    Text("Apply Version", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Selected OS Reminder Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(3.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Selected OS",
                        tint = HoloBlueLight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = osName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Base: ${selectedOs?.defaultAndroidBase ?: "AOSP"} | ${selectedOs?.category?.displayName ?: ""}",
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // Version Options List
            Text(
                text = "AVAILABLE OFFICIAL & COMMUNITY RELEASES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HoloBlueLight,
                letterSpacing = 0.5.sp
            )

            defaultVersions.forEach { version ->
                val isSelected = selectedVersion == version
                val cardBg = if (isSelected) {
                    if (colors.isDark) Color(0xFF142433) else Color(0xFFE3F2FD)
                } else colors.surface

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(3.dp))
                        .background(cardBg)
                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) HoloBlueLight else colors.border, RoundedCornerShape(3.dp))
                        .clickable { onSelectVersion(version) }
                        .padding(12.dp)
                        .testTag("version_option_${version.replace(" ", "_")}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (isSelected) "Selected" else "Unselected",
                                tint = if (isSelected) HoloBlueLight else colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = version,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) HoloBlueLight else colors.textPrimary
                            )
                        }

                        if (isSelected) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HoloBlueLight
                            )
                        }
                    }
                }
            }

            // Custom Version Tag input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.border, RoundedCornerShape(3.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "Or specify custom tag / branch:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customVersionInput,
                            onValueChange = { customVersionInput = it },
                            placeholder = { Text("e.g. nightly-experimental-v3", fontSize = 11.sp, color = colors.textSecondary) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_version_input"),
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
                            onClick = {
                                if (customVersionInput.isNotBlank()) {
                                    onSelectVersion(customVersionInput.trim())
                                }
                            },
                            enabled = customVersionInput.isNotBlank(),
                            shape = RoundedCornerShape(3.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HoloBlueDark,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("apply_custom_version_btn")
                        ) {
                            Text("Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Architecture Downgrade Options
            Text(
                text = "ARCHITECTURE & ABI DOWNGRADE PROFILES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = HoloBlueLight,
                letterSpacing = 0.5.sp
            )

            ArchDowngradeOption.entries.forEach { option ->
                val isArchSelected = selectedArchDowngrade == option
                val cardBg = if (isArchSelected) {
                    if (colors.isDark) Color(0xFF142433) else Color(0xFFE3F2FD)
                } else colors.surface

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(3.dp))
                        .background(cardBg)
                        .border(if (isArchSelected) 2.dp else 1.dp, if (isArchSelected) HoloBlueLight else colors.border, RoundedCornerShape(3.dp))
                        .clickable { onSelectArchDowngrade(option) }
                        .padding(10.dp)
                        .testTag("arch_option_${option.name}")
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isArchSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (isArchSelected) "Selected" else "Unselected",
                                tint = if (isArchSelected) HoloBlueLight else colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isArchSelected) HoloBlueLight else colors.textPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = option.description,
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }

        // Bottom Navigation Buttons
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .border(1.dp, colors.border)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("step_version_back_btn"),
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.textPrimary
                    )
                ) {
                    Text("<- SELECT OS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("step_version_next_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloBlueDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text(
                        text = "NEXT: ADDITIONAL FEATURES ->",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
