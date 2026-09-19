package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The 3 official Holo theme variants as specified:
 * 1. Holo Dark (Theme.Holo)
 * 2. Holo Light (Theme.Holo.Light)
 * 3. Light with Dark Action Bar (Theme.Holo.Light.DarkActionBar)
 */
enum class HoloThemeMode(
    val title: String,
    val systemThemeName: String,
    val description: String
) {
    HOLO_DARK(
        title = "Holo Dark",
        systemThemeName = "Theme.Holo",
        description = "Pitch-black background (#000000), stark white typography, and iconic vibrant Holo Blue (#33B5E5) highlights."
    ),
    HOLO_LIGHT(
        title = "Holo Light",
        systemThemeName = "Theme.Holo.Light",
        description = "Clean light-gray background (#EEEEEE) with dark charcoal text and signature Holo Blue controls."
    ),
    HOLO_LIGHT_DARK_ACTIONBAR(
        title = "Light with Dark Action Bar",
        systemThemeName = "Theme.Holo.Light.DarkActionBar",
        description = "Clean Holo Light body with an inverted, high-contrast dark Holo Action Bar header."
    )
}

data class HoloColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val divider: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val accentDark: Color,
    val actionBarBackground: Color,
    val actionBarText: Color,
    val isDark: Boolean,
    val mode: HoloThemeMode
)

val LocalHoloColors = staticCompositionLocalOf {
    HoloColors(
        background = HoloDarkBackground,
        surface = HoloDarkSurface,
        surfaceVariant = HoloDarkSurfaceVariant,
        border = HoloDarkBorder,
        divider = HoloDarkDivider,
        textPrimary = HoloDarkTextPrimary,
        textSecondary = HoloDarkTextSecondary,
        accent = HoloBlueLight,
        accentDark = HoloBlueDark,
        actionBarBackground = HoloDarkActionBar,
        actionBarText = HoloDarkTextPrimary,
        isDark = true,
        mode = HoloThemeMode.HOLO_DARK
    )
}

object HoloTheme {
    val colors: HoloColors
        @Composable
        @ReadOnlyComposable
        get() = LocalHoloColors.current
}

// Holo Dark Material3 Color Scheme
private val HoloDarkM3Scheme = darkColorScheme(
    primary = HoloBlueLight,
    onPrimary = Color.Black,
    primaryContainer = HoloBlueDark,
    onPrimaryContainer = Color.White,
    secondary = HoloBlueLight,
    onSecondary = Color.Black,
    secondaryContainer = HoloDarkSurfaceVariant,
    onSecondaryContainer = Color.White,
    tertiary = HoloGreenLight,
    background = HoloDarkBackground,
    onBackground = HoloDarkTextPrimary,
    surface = HoloDarkSurface,
    onSurface = HoloDarkTextPrimary,
    surfaceVariant = HoloDarkSurfaceVariant,
    onSurfaceVariant = HoloDarkTextSecondary,
    outline = HoloDarkBorder
)

// Holo Light Material3 Color Scheme
private val HoloLightM3Scheme = lightColorScheme(
    primary = HoloBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F0FA),
    onPrimaryContainer = HoloBlueDark,
    secondary = HoloBlueDark,
    onSecondary = Color.White,
    secondaryContainer = HoloLightSurfaceVariant,
    onSecondaryContainer = HoloLightTextPrimary,
    tertiary = HoloGreenDark,
    background = HoloLightBackground,
    onBackground = HoloLightTextPrimary,
    surface = HoloLightSurface,
    onSurface = HoloLightTextPrimary,
    surfaceVariant = HoloLightSurfaceVariant,
    onSurfaceVariant = HoloLightTextSecondary,
    outline = HoloLightBorder
)

@Composable
fun HoloApplicationTheme(
    themeMode: HoloThemeMode = HoloThemeMode.HOLO_DARK,
    content: @Composable () -> Unit
) {
    val holoColors = when (themeMode) {
        HoloThemeMode.HOLO_DARK -> HoloColors(
            background = HoloDarkBackground,
            surface = HoloDarkSurface,
            surfaceVariant = HoloDarkSurfaceVariant,
            border = HoloDarkBorder,
            divider = HoloDarkDivider,
            textPrimary = HoloDarkTextPrimary,
            textSecondary = HoloDarkTextSecondary,
            accent = HoloBlueLight,
            accentDark = HoloBlueDark,
            actionBarBackground = HoloDarkActionBar,
            actionBarText = HoloDarkTextPrimary,
            isDark = true,
            mode = themeMode
        )
        HoloThemeMode.HOLO_LIGHT -> HoloColors(
            background = HoloLightBackground,
            surface = HoloLightSurface,
            surfaceVariant = HoloLightSurfaceVariant,
            border = HoloLightBorder,
            divider = HoloLightDivider,
            textPrimary = HoloLightTextPrimary,
            textSecondary = HoloLightTextSecondary,
            accent = HoloBlueDark,
            accentDark = HoloBlueLight,
            actionBarBackground = HoloLightActionBar,
            actionBarText = HoloLightTextPrimary,
            isDark = false,
            mode = themeMode
        )
        HoloThemeMode.HOLO_LIGHT_DARK_ACTIONBAR -> HoloColors(
            background = HoloLightBackground,
            surface = HoloLightSurface,
            surfaceVariant = HoloLightSurfaceVariant,
            border = HoloLightBorder,
            divider = HoloLightDivider,
            textPrimary = HoloLightTextPrimary,
            textSecondary = HoloLightTextSecondary,
            accent = HoloBlueLight,
            accentDark = HoloBlueDark,
            actionBarBackground = HoloDarkActionBar,
            actionBarText = HoloDarkTextPrimary,
            isDark = false,
            mode = themeMode
        )
    }

    val m3Scheme = if (themeMode == HoloThemeMode.HOLO_DARK) HoloDarkM3Scheme else HoloLightM3Scheme

    CompositionLocalProvider(LocalHoloColors provides holoColors) {
        MaterialTheme(
            colorScheme = m3Scheme,
            typography = Typography,
            content = content
        )
    }
}
