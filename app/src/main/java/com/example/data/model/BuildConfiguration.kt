package com.example.data.model

import com.example.ui.theme.HoloThemeMode

enum class TargetType(val displayName: String) {
    CUSTOM_OS("Custom Operating System / ROM"),
    RECOVERY("Custom Recovery Environment")
}

enum class MagiskOption(val label: String, val versionTag: String, val description: String) {
    NONE("Stock / Unrooted (No Magisk)", "none", "Keep standard unrooted partition state without SU binaries"),
    ALL_ROOTS("All-in-One Multi-Root Suite (Kitsune + Magisk Alpha + KernelSU + APatch + SuperSU)", "all_roots", "Universal multi-root injector bundle packaging Magisk v27, Kitsune Mask, Magisk Alpha, KernelSU daemon, APatch KernelPatch module, and SuperSU fallback."),
    MAGISK_DELTA("Kitsune Mask (Magisk Delta)", "kitsune", "Stealth root fork with isolated process Zygisk and integrated MagiskHide for banking apps"),
    MAGISK_ALPHA("Magisk Alpha (Bleeding Edge)", "alpha", "Cutting-edge upstream Canary build with experimental kernel-hook modules"),
    KERNEL_SU("KernelSU (GKI Kernel-Space Root)", "kernelsu", "Next-generation kernel-level su provider embedded directly into GKI kernels"),
    APATCH("APatch (KernelPatch & SuperKey)", "apatch", "KernelPatch-based root solution providing fine-grained SuperKey authorization"),
    SUPERSU("SuperSU v2.82 SR5 (Chainfire Historic)", "supersu", "The historic benchmark superuser suite for classic Android 2.3 Gingerbread to 7.1 Nougat"),
    MAGISK_V27_0("Magisk v27.0 (Stable / Canary)", "v27.0", "Latest generation with 64-bit Zygisk injection, modernized daemon, and Android 8-15 support"),
    MAGISK_V26_4("Magisk v26.4 (LTS Release)", "v26.4", "Rock-solid LTS release with broad legacy kernel & SELinux compatibility"),
    MAGISK_V25_2("Magisk v25.2 (Legacy Magisk)", "v25.2", "Classic Magisk version tailored for pre-Android 8 legacy devices & early systemless roots")
}

enum class MagiskPatchMode(val label: String, val description: String) {
    RAMDISK("Direct Boot Ramdisk Patch", "Patches boot.img ramdisk to mount magiskinit on /init"),
    RECOVERY_RAMDISK("Recovery Ramdisk Hijack", "For devices lacking separate ramdisk; hijacks recovery init on power-up"),
    FLASHABLE_ZIP("Flashable Standalone ZIP Injector", "Generates an independent Edify ZIP that installs root via custom recovery")
}

enum class MagiskRootAccess(val label: String) {
    APPS_AND_ADB("Apps and ADB (Full Access)"),
    APPS_ONLY("Apps Only (No ADB root)"),
    ADB_ONLY("ADB Only (No App popups)"),
    DISABLED("Disabled (SU dormant)")
}

data class MagiskCustomization(
    val option: MagiskOption = MagiskOption.NONE,
    val zygiskEnabled: Boolean = true,
    val denyListEnabled: Boolean = true,
    val systemlessHosts: Boolean = true,
    val patchMode: MagiskPatchMode = MagiskPatchMode.RAMDISK,
    val rootAccess: MagiskRootAccess = MagiskRootAccess.APPS_AND_ADB,
    val randomizeStubPkg: Boolean = true,
    val preserveForceEncrypt: Boolean = false,
    val preserveVerity: Boolean = false
)

enum class ArchDowngradeOption(val label: String, val targetAbi: String, val description: String) {
    NATIVE("Native Target Architecture", "native", "Keep the device's original CPU instruction set without downgrade shims."),
    ARM64_TO_ARMV7A("Downgrade: arm64-v8a -> armeabi-v7a", "armeabi-v7a", "Replaces 64-bit binaries with 32-bit ARMv7-A stubs, adjusts build.prop ABI list, and injects 32-bit Bionic linker wrappers for legacy SOCs."),
    ARM64_TO_ARMEABI("Downgrade: arm64-v8a -> armeabi (Legacy)", "armeabi", "Strips NEON/VFP requirements and targets baseline ARMv5TE/ARMv6 instruction sets for ultra-legacy hardware like Cupcake era chips."),
    ARMV7A_TO_ARMEABI("Downgrade: armeabi-v7a -> armeabi", "armeabi", "Removes ARMv7-A NEON requirement for early hardware compatibility (HTC Dream, HTC Magic, MSM7200/7201A).")
}

data class AdditionalFeatures(
    val guiStyle: HoloThemeMode = HoloThemeMode.HOLO_DARK,
    val magiskOption: MagiskOption = MagiskOption.NONE,
    val magiskCustomization: MagiskCustomization = MagiskCustomization(),
    val debloaterScript: Boolean = true,
    val customKernelFlasher: Boolean = true,
    val safeStrapHijack: Boolean = false,
    val busyboxInjection: Boolean = true,
    val microGInjection: Boolean = false,
    val signatureSpoofing: Boolean = false,
    val batterySaverProfile: Boolean = true,
    val viper4AndroidFx: Boolean = true,
    val zramSwapOptimizer: Boolean = true,
    val adawaySystemlessHosts: Boolean = true,
    val fDroidPrivilegedExt: Boolean = true,
    val thermalMitigationTweak: Boolean = false,
    val forceDexPreopt: Boolean = false,
    val appOpsPrivacyManager: Boolean = true,
    val dalvikHeapOptimizer: Boolean = true
) {
    val guiStyleZip: Boolean get() = true
    val magiskModding: Boolean get() = magiskOption != MagiskOption.NONE || magiskCustomization.option != MagiskOption.NONE
    val debloatMinimal: Boolean get() = debloaterScript
}

data class BuildConfiguration(
    val targetType: TargetType = TargetType.CUSTOM_OS,
    val device: DeviceModel = DeviceRepository.devices.first(),
    val targetWifiSsid: String = DeviceRepository.devices.first().defaultTargetWifiSsid,
    val recovery: RecoveryModel? = RecoveryRepository.recoveries.first(),
    val os: OsModel? = OsRepository.operatingSystems.first(),
    val selectedVersion: String = "LineageOS 21 (U)",
    val archDowngrade: ArchDowngradeOption = ArchDowngradeOption.NATIVE,
    val additionalFeatures: AdditionalFeatures = AdditionalFeatures()
)
