package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// Iconic Android Holo Color Palette (Ice Cream Sandwich 4.0 - Jelly Bean)
// =========================================================================

// Signature Holo Blues
val HoloBlueLight = Color(0xFF33B5E5) // The iconic, vibrant Holo Blue accent (#33B5E5)
val HoloBlueDark = Color(0xFF0099CC)  // Pressed/active Holo Blue (#0099CC)
val HoloBlueBright = Color(0xFF5CD2FF) // High-contrast glowing edges and focus rings

// Holo Functional Accents
val HoloGreenLight = Color(0xFF99CC00)
val HoloGreenDark = Color(0xFF669900)
val HoloRedLight = Color(0xFFFF4444)
val HoloRedDark = Color(0xFFCC0000)
val HoloOrangeLight = Color(0xFFFFBB33)
val HoloOrangeDark = Color(0xFFFF8800)
val HoloPurple = Color(0xFFAA66CC)

// -------------------------------------------------------------------------
// 1. Holo Dark (Theme.Holo)
// Flagship Honeycomb/ICS/Jelly Bean aesthetic engineered for OLED efficiency.
// Pitch-black / dark charcoal background, stark white typography, glowing blue accents.
// -------------------------------------------------------------------------
val HoloDarkBackground = Color(0xFF000000)      // Pitch-black OLED background (#000000)
val HoloDarkBackgroundAlt = Color(0xFF111111)   // Very dark charcoal (#111111)
val HoloDarkSurface = Color(0xFF141414)         // Flat industrial dark card surface
val HoloDarkSurfaceVariant = Color(0xFF1F1F1F)  // Segmented control / item background
val HoloDarkBorder = Color(0xFF2E2E2E)          // Sharp industrial border
val HoloDarkDivider = Color(0xFF262626)         // Thin subtle divider
val HoloDarkTextPrimary = Color(0xFFFFFFFF)     // Stark white typography
val HoloDarkTextSecondary = Color(0xFF9E9E9E)   // Clean secondary neutral text
val HoloDarkActionBar = Color(0xFF0A0A0A)       // Flat dark top action bar header

// -------------------------------------------------------------------------
// 2. Holo Light (Theme.Holo.Light)
// Bright alternative for paper-like readability. Clean light-gray/off-white background,
// dark charcoal text, signature Holo Blue toggles, checkboxes, and buttons.
// -------------------------------------------------------------------------
val HoloLightBackground = Color(0xFFEEEEEE)     // Clean light-gray background (#EEEEEE)
val HoloLightSurface = Color(0xFFFFFFFF)        // Paper-like white card surface (#FFFFFF)
val HoloLightSurfaceVariant = Color(0xFFE4E4E4) // Control / segmented background
val HoloLightBorder = Color(0xFFCCCCCC)         // Subtle gray border
val HoloLightDivider = Color(0xFFD4D4D4)        // Section divider line
val HoloLightTextPrimary = Color(0xFF222222)    // Dark charcoal / black text
val HoloLightTextSecondary = Color(0xFF666666)  // Muted gray text
val HoloLightActionBar = Color(0xFFF7F7F7)      // Light gray / white action bar header

// -------------------------------------------------------------------------
// 3. Light with Dark Action Bar (Theme.Holo.Light.DarkActionBar)
// Hybrid variant: Holo Light body with inverted Holo Dark Action Bar header.
// -------------------------------------------------------------------------
// Content area uses HoloLightBackground, HoloLightSurface, HoloLightTextPrimary
// Header uses HoloDarkActionBar, HoloDarkTextPrimary, and HoloBlueLight highlights!

// Terminal & Log Viewer Palette
val HoloTerminalBg = Color(0xFF000000)
val HoloTerminalGreen = Color(0xFF99CC00)
val HoloTerminalCyan = Color(0xFF33B5E5)
val HoloTerminalYellow = Color(0xFFFFBB33)
