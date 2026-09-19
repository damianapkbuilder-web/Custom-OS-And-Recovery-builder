package com.example.ui.components

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.data.model.OsCategory
import com.example.data.model.OsModel
import com.example.data.model.OsRepository
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloGreenLight
import com.example.ui.theme.HoloOrangeLight
import com.example.ui.theme.HoloPurple
import com.example.ui.theme.HoloTheme

import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import com.example.data.repository.DiscoveredOnlineItem
import com.example.data.repository.WebSearchDiscoveryEngine
import kotlinx.coroutines.launch

@Composable
fun OsSelectionStep(
    selectedOs: OsModel?,
    onSelectOs: (OsModel) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = HoloTheme.colors
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<OsCategory?>(null) }
    var isSearchingWeb by remember { mutableStateOf(false) }
    var webSearchResults by remember { mutableStateOf<List<DiscoveredOnlineItem>>(emptyList()) }
    var showWebResults by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun triggerWebSearch() {
        scope.launch {
            isSearchingWeb = true
            webSearchResults = WebSearchDiscoveryEngine.searchOsOnline(searchQuery)
            isSearchingWeb = false
            showWebResults = true
        }
    }

    val allOsList = remember { OsRepository.operatingSystems }

    val filteredList = remember(searchQuery, selectedCategoryFilter) {
        allOsList.filter { os ->
            val matchesCategory = selectedCategoryFilter == null || os.category == selectedCategoryFilter
            val matchesSearch = if (searchQuery.isBlank()) true else {
                os.name.contains(searchQuery, ignoreCase = true) ||
                os.shortName.contains(searchQuery, ignoreCase = true) ||
                os.description.contains(searchQuery, ignoreCase = true) ||
                os.defaultAndroidBase.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("step_os_selection")
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
                    text = "STEP 3: SELECT OPERATING SYSTEM",
                    color = HoloBlueLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Choose from supported custom ROMs (including LineageOS 22, 23, 24) or search online.",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Search field with Web Search button
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
                            .testTag("os_search_input"),
                        placeholder = {
                            Text("Search ROMs (LineageOS 22, 23, 24...)", fontSize = 11.sp, color = colors.textSecondary)
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
                        modifier = Modifier.testTag("os_web_search_btn"),
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

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isAll = selectedCategoryFilter == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isAll) HoloBlueDark else colors.surfaceVariant)
                            .border(1.dp, if (isAll) HoloBlueLight else colors.border, RoundedCornerShape(3.dp))
                            .clickable { selectedCategoryFilter = null }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "All (${allOsList.size})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAll) Color.White else colors.textPrimary
                        )
                    }

                    OsCategory.entries.forEach { cat ->
                        val isCatSelected = selectedCategoryFilter == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isCatSelected) HoloBlueDark else colors.surfaceVariant)
                                .border(1.dp, if (isCatSelected) HoloBlueLight else colors.border, RoundedCornerShape(3.dp))
                            .clickable { selectedCategoryFilter = if (isCatSelected) null else cat }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cat.displayName,
                                fontSize = 10.sp,
                                fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCatSelected) Color.White else colors.textPrimary
                            )
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
                    .testTag("os_web_results_card")
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
                            Text("DISCOVERED ONLINE ROM REPOSITORIES (LATEST)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HoloBlueLight)
                        }
                        Text(
                            text = "Dismiss",
                            fontSize = 10.sp,
                            color = HoloOrangeLight,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showWebResults = false }
                        )
                    }

                    webSearchResults.take(3).forEach { item ->
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
                                    Text("Source: ${item.sourceRepo} • Channel: ${item.channel}", fontSize = 9.sp, color = colors.textSecondary)
                                    Text(item.description, fontSize = 9.sp, color = if (colors.isDark) Color(0xFF81C784) else Color(0xFF388E3C))
                                }
                                Button(
                                    onClick = {
                                        val matched = allOsList.find { it.id == "lineageos" } ?: allOsList.first()
                                        onSelectOs(matched.copy(defaultVersions = listOf(item.versionTag) + matched.defaultVersions))
                                        showWebResults = false
                                    },
                                    shape = RoundedCornerShape(2.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = HoloBlueDark, contentColor = Color.White),
                                    modifier = Modifier.testTag("apply_web_os_${item.versionTag}")
                                ) {
                                    Text("Select OS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // OS List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredList, key = { it.id }) { os ->
                val isSelected = selectedOs?.id == os.id

                val cardBg = when {
                    isSelected -> if (colors.isDark) Color(0xFF142433) else Color(0xFFE3F2FD)
                    else -> colors.surface
                }
                val cardBorder = if (isSelected) HoloBlueLight else colors.border

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(3.dp))
                        .background(cardBg)
                        .border(if (isSelected) 2.dp else 1.dp, cardBorder, RoundedCornerShape(3.dp))
                        .clickable { onSelectOs(os) }
                        .padding(12.dp)
                        .testTag("os_item_${os.id}")
                ) {
                    Column {
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
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = if (isSelected) "Selected" else "Unselected",
                                    tint = if (isSelected) HoloBlueLight else colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = os.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) HoloBlueLight else colors.textPrimary
                                    )
                                    Text(
                                        text = "Base: ${os.defaultAndroidBase}",
                                        fontSize = 11.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            // Category Tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (colors.isDark) Color(0xFF1A1A1A) else Color(0xFFEEEEEE))
                                    .border(1.dp, colors.border, RoundedCornerShape(2.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = os.category.displayName,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (colors.isDark) HoloBlueLight else HoloBlueDark
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = os.description,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
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
                        .testTag("step_os_back_btn"),
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.textPrimary
                    )
                ) {
                    Text("<- RECOVERY", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("step_os_next_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoloBlueDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text(
                        text = "NEXT: SELECT VERSION ->",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
