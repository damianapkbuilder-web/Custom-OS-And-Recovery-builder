package com.example.data.model

enum class OsCategory(val displayName: String) {
    ANCESTOR_LIBRE("Ancestor & Libre Bases"),
    FEATURE_RICH("Feature-Rich & Themed Custom ROMs"),
    OEM_PIXEL_PORT("OEM-Replicating & Pixel-Port ROMs"),
    PERFORMANCE_MINIMALIST("Performance & Minimalist Builds"),
    EXPERIMENTAL_RETRO("Experimental, Retro & Specialized")
}

data class OsModel(
    val id: String,
    val name: String,
    val shortName: String,
    val category: OsCategory,
    val description: String,
    val defaultVersions: List<String>,
    val defaultAndroidBase: String,
    val isNonAndroid: Boolean = false
)

object OsRepository {
    val operatingSystems = listOf(
        // Ancestor & Libre Bases
        OsModel(
            id = "cyanogenmod",
            name = "CyanogenMod (CM)",
            shortName = "CM",
            category = OsCategory.ANCESTOR_LIBRE,
            description = "The undisputed grandfather of all custom ROMs, which started the entire aftermarket modification scene.",
            defaultVersions = listOf("CM 3.6.8 (Cupcake 1.5)", "CM 4.2.15 (Donut)", "CM 7.2 (Gingerbread)", "CM 10.2 (Jelly Bean)", "CM 13.0 (Marshmallow)"),
            defaultAndroidBase = "Android 1.5 - 6.0"
        ),
        OsModel(
            id = "lineageos",
            name = "LineageOS",
            shortName = "LineageOS",
            category = OsCategory.ANCESTOR_LIBRE,
            description = "The direct continuation of CyanogenMod, serving as the modern base code for a massive percentage of custom software today.",
            defaultVersions = listOf(
                "LineageOS 14.1 (Nougat)",
                "LineageOS 17.1 (Q)",
                "LineageOS 18.1 (R)",
                "LineageOS 20 (T)",
                "LineageOS 21 (U)",
                "LineageOS 22 (Android 15)",
                "LineageOS 23 (Android 16)",
                "LineageOS 24 (Android 17 / Next-Gen)"
            ),
            defaultAndroidBase = "Android 7.1 - 17+"
        ),
        OsModel(
            id = "cyanogenos",
            name = "CyanogenOS",
            shortName = "CyanogenOS",
            category = OsCategory.ANCESTOR_LIBRE,
            description = "The commercialized version used by early OnePlus and Wileyfox devices.",
            defaultVersions = listOf("COS 11S (KitKat)", "COS 12 (Lollipop)", "COS 13.1 (Marshmallow)"),
            defaultAndroidBase = "Android 4.4 - 6.0"
        ),
        OsModel(
            id = "replicant",
            name = "Replicant",
            shortName = "Replicant",
            category = OsCategory.ANCESTOR_LIBRE,
            description = "A 100% free-software (libre) custom Android distribution that completely purges all proprietary binary blobs and drivers.",
            defaultVersions = listOf("Replicant 4.2 Libre", "Replicant 6.0 FSF-Endorsed"),
            defaultAndroidBase = "Android 4.2 - 6.0"
        ),

        // Feature-Rich & Themed Custom ROMs
        OsModel(
            id = "resurrection_remix",
            name = "Resurrection Remix (RR)",
            shortName = "Resurrection Remix",
            category = OsCategory.FEATURE_RICH,
            description = "Legendary for offering virtually every customization toggle, animation tweak, and status bar mod known to Android.",
            defaultVersions = listOf("RR 5.8.5 (Nougat)", "RR 7.0.2 (Pie)", "RR 8.7.3 (Q)"),
            defaultAndroidBase = "Android 7.1 - 10"
        ),
        OsModel(
            id = "evolution_x",
            name = "Evolution X",
            shortName = "Evolution X",
            category = OsCategory.FEATURE_RICH,
            description = "Popular for meticulously replicating the Pixel UI while packing in heavy custom theming and system extras.",
            defaultVersions = listOf("Evolution X 5.9 (R)", "Evolution X 7.9 (T)", "Evolution X 8.4 (U)"),
            defaultAndroidBase = "Android 11 - 14"
        ),
        OsModel(
            id = "paranoid_android",
            name = "Paranoid Android (PA)",
            shortName = "Paranoid Android",
            category = OsCategory.FEATURE_RICH,
            description = "Pioneers of unique UI paradigms, including 'Pie Controls' and dynamic per-app color scaling.",
            defaultVersions = listOf("PA 3.99 Halo (Jelly Bean)", "PA 4.4 Peek", "PA Quartz (Android 10)", "PA Topaz (Android 13)"),
            defaultAndroidBase = "Android 4.3 - 13"
        ),
        OsModel(
            id = "aokp",
            name = "AOKP (Android Open Kang Project)",
            shortName = "AOKP",
            category = OsCategory.FEATURE_RICH,
            description = "Famous for early innovations like the custom navigation ring, performance control centers, and 'ribbons'.",
            defaultVersions = listOf("AOKP Milestone 6 (ICS)", "AOKP jb-mr2", "AOKP nougat-release"),
            defaultAndroidBase = "Android 4.0 - 7.1"
        ),
        OsModel(
            id = "omnirom",
            name = "OmniROM",
            shortName = "OmniROM",
            category = OsCategory.FEATURE_RICH,
            description = "Known for multi-window features, customized quick settings, and being a clean, stable daily driver.",
            defaultVersions = listOf("OmniROM 4.4.4", "OmniROM 7.1.2", "OmniROM 9.0", "OmniROM 13"),
            defaultAndroidBase = "Android 4.4 - 13"
        ),
        OsModel(
            id = "dirty_unicorns",
            name = "Dirty Unicorns (DU)",
            shortName = "Dirty Unicorns",
            category = OsCategory.FEATURE_RICH,
            description = "A tight-knit developer community known for ultra-stable builds and distinct visual tweaks.",
            defaultVersions = listOf("DU 10.6 (Marshmallow)", "DU 12.5 (Oreo)", "DU 14.7 (Q)"),
            defaultAndroidBase = "Android 6.0 - 10"
        ),
        OsModel(
            id = "havoc_os",
            name = "Havoc-OS",
            shortName = "Havoc-OS",
            category = OsCategory.FEATURE_RICH,
            description = "Modern powerhouse ROM focused on battery efficiency, custom gestures, and extensive overlay engines.",
            defaultVersions = listOf("Havoc-OS 3.12 (Android 10)", "Havoc-OS 4.16 (Android 11)", "Havoc-OS 5.0 (Android 12)"),
            defaultAndroidBase = "Android 10 - 12"
        ),
        OsModel(
            id = "blissroms",
            name = "BlissROMs",
            shortName = "BlissROMs",
            category = OsCategory.FEATURE_RICH,
            description = "Modern powerhouse ROM focused on rich customization, comprehensive sound profiles, and launcher tweaks.",
            defaultVersions = listOf("Bliss 12.12 (Q)", "Bliss 14.8 (R)", "Bliss 17.0 (U)"),
            defaultAndroidBase = "Android 10 - 14"
        ),
        OsModel(
            id = "crdroid",
            name = "crDroid",
            shortName = "crDroid",
            category = OsCategory.FEATURE_RICH,
            description = "Highly optimized, battery-first ROM with extensive interface mod settings built upon LineageOS base.",
            defaultVersions = listOf("crDroid 6.24 (Android 10)", "crDroid 7.16 (Android 11)", "crDroid 9.10 (Android 13)", "crDroid 10.5 (Android 14)"),
            defaultAndroidBase = "Android 10 - 14"
        ),

        // OEM-Replicating & Pixel-Port ROMs
        OsModel(
            id = "pixel_experience",
            name = "Pixel Experience / Pixel Experience Plus",
            shortName = "Pixel Experience",
            category = OsCategory.OEM_PIXEL_PORT,
            description = "Built exclusively to port Google Pixel stock software, camera processing, and launcher features onto non-Pixel hardware.",
            defaultVersions = listOf("PE 10.0 Plus", "PE 12.1 Plus", "PE 13.0 Plus", "PE 14.0 Stable"),
            defaultAndroidBase = "Android 10 - 14"
        ),
        OsModel(
            id = "pixel_extended",
            name = "Pixel Extended",
            shortName = "Pixel Extended",
            category = OsCategory.OEM_PIXEL_PORT,
            description = "Lighter, optimized take on the clean Pixel interface with extra stability and performance tweaks.",
            defaultVersions = listOf("PEX 4.5 (Android 12)", "PEX 5.8 (Android 13)"),
            defaultAndroidBase = "Android 12 - 13"
        ),
        OsModel(
            id = "dot_os",
            name = "DotOS",
            shortName = "DotOS",
            category = OsCategory.OEM_PIXEL_PORT,
            description = "Distinctive custom UI with rounded card aesthetics, redesigned quick settings, and wallpaper accent styling.",
            defaultVersions = listOf("DotOS 5.1.3 (R)", "DotOS 5.2 (S)"),
            defaultAndroidBase = "Android 11 - 12"
        ),
        OsModel(
            id = "arrow_os",
            name = "ArrowOS",
            shortName = "ArrowOS",
            category = OsCategory.OEM_PIXEL_PORT,
            description = "Clean, minimalist, and battery-optimized AOSP-based experience with pure performance focus.",
            defaultVersions = listOf("ArrowOS 11.0 (Official)", "ArrowOS 12.1", "ArrowOS 13.1"),
            defaultAndroidBase = "Android 11 - 13"
        ),
        OsModel(
            id = "miui_eu",
            name = "MIUI.EU / Xiaomi.EU",
            shortName = "MIUI.EU",
            category = OsCategory.OEM_PIXEL_PORT,
            description = "Community-cleaned, localized, and debloated versions of Xiaomi's MIUI/HyperOS meant for global markets.",
            defaultVersions = listOf("MIUI 12.5 Enhanced EU", "MIUI 14 Global EU", "Xiaomi HyperOS 1.0 EU"),
            defaultAndroidBase = "Android 11 - 14"
        ),

        // Performance, Kernel-Level, and Minimalist Builds
        OsModel(
            id = "aosp_pure",
            name = "AOSP (Pure / Vanilla)",
            shortName = "Pure AOSP",
            category = OsCategory.PERFORMANCE_MINIMALIST,
            description = "Raw source code compiled directly without Google Play Services or custom skins.",
            defaultVersions = listOf("AOSP 1.5 Cupcake", "AOSP 2.3 Gingerbread", "AOSP 4.4 KitKat", "AOSP 7.1 Nougat", "AOSP 11", "AOSP 14", "AOSP 15 Vanilla"),
            defaultAndroidBase = "Android 1.5 - 15"
        ),
        OsModel(
            id = "lineage_microg",
            name = "LineageOS for microG",
            shortName = "Lineage + microG",
            category = OsCategory.PERFORMANCE_MINIMALIST,
            description = "Pre-configured with open-source replacement libraries for Google Play Services to maintain privacy without breaking app compatibility.",
            defaultVersions = listOf(
                "Lineage-18.1-microG",
                "Lineage-19.1-microG",
                "Lineage-20-microG",
                "Lineage-21-microG",
                "Lineage-22-microG (Android 15)",
                "Lineage-23-microG (Android 16)",
                "Lineage-24-microG (Android 17)"
            ),
            defaultAndroidBase = "Android 11 - 17+"
        ),
        OsModel(
            id = "calyxos",
            name = "CalyxOS",
            shortName = "CalyxOS",
            category = OsCategory.PERFORMANCE_MINIMALIST,
            description = "Security-focused custom operating system designed to harden hardware-backed encryption, sandbox apps, and strip out telemetry.",
            defaultVersions = listOf("CalyxOS 3.8.0 (Android 12)", "CalyxOS 4.14.0 (Android 13)", "CalyxOS 5.5.0 (Android 14)"),
            defaultAndroidBase = "Android 12 - 14"
        ),
        OsModel(
            id = "grapheneos",
            name = "GrapheneOS",
            shortName = "GrapheneOS",
            category = OsCategory.PERFORMANCE_MINIMALIST,
            description = "Security-focused custom operating systems designed to harden hardware-backed encryption, sandbox apps, and strip out telemetry (primarily targeted at Google Pixel devices).",
            defaultVersions = listOf("GrapheneOS 2023 Official", "GrapheneOS 2024 Stable Hardened"),
            defaultAndroidBase = "Android 13 - 14"
        ),
        OsModel(
            id = "aicp",
            name = "AICP (Android Ice Cold Project)",
            shortName = "AICP",
            category = OsCategory.PERFORMANCE_MINIMALIST,
            description = "A long-running custom ROM focused heavily on performance tuning and custom kernel integration.",
            defaultVersions = listOf("AICP 11.0 (Marshmallow)", "AICP 14.0 (Pie)", "AICP 16.1 (Android 11)"),
            defaultAndroidBase = "Android 6.0 - 11"
        ),

        // Experimental, Retro & Specialized
        OsModel(
            id = "ubuntu_touch",
            name = "Ubuntu Touch (UBports)",
            shortName = "Ubuntu Touch",
            category = OsCategory.EXPERIMENTAL_RETRO,
            description = "Touch-friendly mobile Linux OS with convergence support (standalone non-Android distribution).",
            defaultVersions = listOf("OTA-20 Legacy", "OTA-24 Xenial", "20.04 LTS Focal Touch"),
            defaultAndroidBase = "Ubuntu Linux Core",
            isNonAndroid = true
        ),
        OsModel(
            id = "iphroid",
            name = "iPhroid & iOS Port Projects",
            shortName = "iPhroid iOS Port",
            category = OsCategory.EXPERIMENTAL_RETRO,
            description = "Community hobbyist builds attempting to map iOS-style directory structures, package managers, and visual SpringBoard themes onto Android partitions.",
            defaultVersions = listOf("iPhroid OpeniBoot v0.8", "iOS Port Theme Engine v1.4"),
            defaultAndroidBase = "Android / OpeniBoot Hybrid"
        ),
        OsModel(
            id = "aosp_custom_fork",
            name = "AOSP Source Builds (Custom Forks)",
            shortName = "AOSP Custom Fork",
            category = OsCategory.EXPERIMENTAL_RETRO,
            description = "Independent hobbyist projects compiled directly from source trees (such as experimental AOSP branches) for target architectures, emulator environments, or custom hardware modifications.",
            defaultVersions = listOf("Custom Bare-Metal v1.0", "Legacy Architecture Mod v2.1"),
            defaultAndroidBase = "AOSP Source Tree"
        ),
        OsModel(
            id = "cupcake",
            name = "Cupcake (Android 1.5)",
            shortName = "Cupcake",
            category = OsCategory.EXPERIMENTAL_RETRO,
            description = "The historical root of modern Android releases. Ultra-lightweight system with foundational Linux 2.6 kernel and ARMv5TE baseline compatibility.",
            defaultVersions = listOf("Cupcake 1.5 Official", "Cupcake ADP1 Root Edition", "TheDude Cupcake 0.9"),
            defaultAndroidBase = "Android 1.5"
        ),
        OsModel(
            id = "havoc_bliss_crdroid",
            name = "Havoc-OS / BlissROMs / crDroid",
            shortName = "Havoc / Bliss / crDroid",
            category = OsCategory.FEATURE_RICH,
            description = "Unified custom performance trio offering deep customization, aggressive battery saving profiles, and LineageOS-based mod engines.",
            defaultVersions = listOf("v14.0 Unified Mod Edition", "v13.0 Custom Suite", "v12.5 Stable Trio"),
            defaultAndroidBase = "Android 12 - 14"
        ),
        OsModel(
            id = "pixel_dot_arrow",
            name = "Pixel Extended / DotOS / ArrowOS",
            shortName = "PEX / Dot / Arrow",
            category = OsCategory.OEM_PIXEL_PORT,
            description = "Lightweight Pixel-esque and clean AOSP design bundle with rounded UI widgets, dynamic monet theming, and lean system services.",
            defaultVersions = listOf("AOSP Clean Edition 13.0", "Pixel-Dot Unified 12.1", "Arrow Speed Edition 11.0"),
            defaultAndroidBase = "Android 11 - 13"
        )
    )
}
