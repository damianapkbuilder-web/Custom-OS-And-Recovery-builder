package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.HoloActionBar
import com.example.ui.screens.BatterySyncScreen
import com.example.ui.screens.BuilderScreen
import com.example.ui.screens.SavedBuildsScreen
import com.example.ui.theme.HoloApplicationTheme
import com.example.ui.theme.HoloBlueDark
import com.example.ui.theme.HoloBlueLight
import com.example.ui.theme.HoloOrangeLight
import com.example.ui.theme.HoloTheme

sealed class ScreenTab(val title: String, val icon: ImageVector, val tag: String) {
    data object Builder : ScreenTab("Builder", Icons.Default.Build, "nav_tab_builder")
    data object Flashables : ScreenTab("Flashables", Icons.Default.FolderZip, "nav_tab_flashables")
    data object PowerSync : ScreenTab("Power & Sync", Icons.Default.BatteryChargingFull, "nav_tab_powersync")
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appTheme by viewModel.appThemeMode.collectAsStateWithLifecycle()
            HoloApplicationTheme(themeMode = appTheme) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = remember {
        listOf(
            ScreenTab.Builder,
            ScreenTab.Flashables,
            ScreenTab.PowerSync
        )
    }

    val currentTheme by viewModel.appThemeMode.collectAsStateWithLifecycle()
    val savedBuilds by viewModel.savedBuilds.collectAsStateWithLifecycle()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val batteryState by viewModel.batteryState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val colors = HoloTheme.colors

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            HoloActionBar(
                currentTheme = currentTheme,
                onThemeSelected = { newTheme ->
                    viewModel.setAppTheme(newTheme)
                },
                isWifiConnected = batteryState.isWifiRequirementMet,
                isWifiSimulated = batteryState.isWifiSimulated,
                onToggleWifiSimulation = { viewModel.toggleWifiSimulation(it) },
                onApplyHeaviestPreset = { viewModel.configureHeaviestPreset() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = if (colors.isDark) Color(0xFF0F0F0F) else Color(0xFFE5E5E5),
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar")
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            if (tab is ScreenTab.Flashables && savedBuilds.isNotEmpty()) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = HoloBlueLight, contentColor = Color.Black) {
                                            Text("${savedBuilds.size}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                ) {
                                    Icon(imageVector = tab.icon, contentDescription = tab.title)
                                }
                            } else if (tab is ScreenTab.PowerSync && pendingSyncCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = HoloOrangeLight, contentColor = Color.Black) {
                                            Text("$pendingSyncCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                ) {
                                    Icon(imageVector = tab.icon, contentDescription = tab.title)
                                }
                            } else {
                                Icon(imageVector = tab.icon, contentDescription = tab.title)
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (colors.isDark) Color.Black else Color.White,
                            selectedTextColor = if (colors.isDark) HoloBlueLight else HoloBlueDark,
                            indicatorColor = HoloBlueLight,
                            unselectedIconColor = colors.textSecondary,
                            unselectedTextColor = colors.textSecondary
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> BuilderScreen(viewModel = viewModel)
                1 -> SavedBuildsScreen(viewModel = viewModel)
                2 -> BatterySyncScreen(viewModel = viewModel)
            }
        }
    }
}
