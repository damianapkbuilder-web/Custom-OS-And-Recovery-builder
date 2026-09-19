package com.example.data.model

data class RecoveryModel(
    val id: String,
    val name: String,
    val shortName: String,
    val description: String,
    val defaultVersions: List<String>,
    val navigationType: String,
    val supportsThemes: Boolean = false,
    val supportsLockedBootloader: Boolean = false
)

object RecoveryRepository {
    val recoveries = listOf(
        RecoveryModel(
            id = "amon_ra",
            name = "Amon_RA Recovery",
            shortName = "Amon_RA",
            description = "An early button-navigated recovery used alongside the first Android root tools.",
            defaultVersions = listOf("v1.6.2", "v2.0.0", "v3.0.6-CyanogenMod"),
            navigationType = "Physical Trackball / Volume Keys"
        ),
        RecoveryModel(
            id = "cwm",
            name = "ClockworkMod (CWM)",
            shortName = "CWM",
            description = "The legendary early touch-alternative recovery controlled with physical keys.",
            defaultVersions = listOf("v2.5.0.7 Classic", "v5.0.2.0", "v6.0.4.7 Touch"),
            navigationType = "Physical Keys / Capacitive Nav"
        ),
        RecoveryModel(
            id = "twrp",
            name = "Team Win Recovery Project (TWRP)",
            shortName = "TWRP",
            description = "The historic project that brought full touchscreen navigation to custom recoveries.",
            defaultVersions = listOf("v2.8.7.0 (Legacy)", "v3.3.1 (Unified)", "v3.7.0", "v3.8.0 Modern"),
            navigationType = "Full Touchscreen GUI",
            supportsThemes = true
        ),
        RecoveryModel(
            id = "orangefox",
            name = "OrangeFox Recovery",
            shortName = "OrangeFox",
            description = "A popular TWRP-based recovery adding advanced theme support and built-in patch tools.",
            defaultVersions = listOf("R11.1 Stable", "R12.0 FoxUI"),
            navigationType = "FoxUI Touch Interface",
            supportsThemes = true
        ),
        RecoveryModel(
            id = "pbrp",
            name = "PitchBlack Recovery Project (PBRP)",
            shortName = "PBRP",
            description = "A dark-themed TWRP fork with built-in decryption and system cleaning tools.",
            defaultVersions = listOf("v3.1.0 Dark", "v4.0.0 PBRP"),
            navigationType = "Dark Touch Engine",
            supportsThemes = true
        ),
        RecoveryModel(
            id = "redwolf",
            name = "RedWolf Recovery",
            shortName = "RedWolf",
            description = "A security-focused TWRP fork built with password protection and automated backup features.",
            defaultVersions = listOf("v027", "v028 Security Mod"),
            navigationType = "Password-Protected Touch"
        ),
        RecoveryModel(
            id = "shrp",
            name = "SkyHawk Recovery (SHRP)",
            shortName = "SHRP",
            description = "A modern menu-driven TWRP alternative with an updated dashboard layout.",
            defaultVersions = listOf("v2.2 Dash", "v3.2 Material"),
            navigationType = "Material Dashboard UI",
            supportsThemes = true
        ),
        RecoveryModel(
            id = "lineage_rec",
            name = "LineageOS Recovery",
            shortName = "Lineage Rec",
            description = "A minimal, official recovery built directly by the LineageOS team strictly for flashing their software.",
            defaultVersions = listOf("18.1 Official", "19.1", "20.0", "21.0 Minimal", "22.0 (Android 15)", "23.0 (Android 16)", "24.0 (Android 17)"),
            navigationType = "Minimal Direct Mode"
        ),
        RecoveryModel(
            id = "safestrap",
            name = "SafeStrap Recovery",
            shortName = "SafeStrap",
            description = "A legacy tool designed to trick locked-bootloader devices into booting custom recovery environments.",
            defaultVersions = listOf("v3.75 2nd-init", "v4.01 Hijack"),
            navigationType = "2nd-init Hijack Touch UI",
            supportsLockedBootloader = true
        ),
        RecoveryModel(
            id = "pterodon",
            name = "Pterodon Recovery",
            shortName = "Pterodon",
            description = "A modular, alternative custom recovery designed to support modern device partitions.",
            defaultVersions = listOf("v2.0 Modular", "v2.5 Dynamic"),
            navigationType = "Modular Modern GUI"
        )
    )
}
