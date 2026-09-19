# Recovery & Custom ROM Packaging Tool

A native Android application built with Kotlin and Jetpack Compose designed for configuring, downloading, patching, rooting, and packaging custom ROMs and custom recoveries.

## Key Features

- **Recovery Packaging**: TWRP, OrangeFox, LineageOS Recovery, PBRP, SHRP, SkyHawk, RedWolf, SafeStrap, and custom recoveries.
- **Operating Systems**: LineageOS 22 (Android 15), 23 (Android 16), 24 (Android 17 / Next-Gen), crDroid, Pixel Experience, Evolution X, Paranoid Android, CarbonROM, and microG editions.
- **Rooting Solutions**:
  - **All-in-One Multi-Root Suite** (Auto-fallback installer)
  - Kitsune Mask (Magisk Delta)
  - Magisk Alpha & Canary
  - KernelSU (GKI)
  - APatch (KernelPatch & SuperKey)
  - SuperSU (v2.82 SR5 Legacy)
- **Audio & Performance Mods**: ViPER4Android FX DSP, ZRAM 2GB Swap, AdAway Hosts, F-Droid Privileged Extension, Thermal Mitigation, DexPreopt ART compilation, and AppOps privacy hardening.
- **Architecture Downgrading**: Support for ARM64-v8a to ARMv7-a NEON translations and 32-bit execution profiles.
- **Device Support**: Multi-vendor device support including Samsung, Google Pixel, Xiaomi / Poco, OnePlus, Motorola, and generic AOSP targets.
- **Direct Storage Integration**: Automatically extracts and packages output ZIPs into `/storage/emulated/0/Download/`.

## Architecture & Tech Stack

- **UI Framework**: Jetpack Compose with Material 3 Design
- **Architecture**: MVVM with Kotlin Coroutines & Flow
- **Persistence**: Room Database with KSP
- **Target SDK**: Android 15 (API 35) / Min SDK 24

## Building from Source

```bash
# Build Debug APK
./gradlew assembleDebug

# Run Unit & Robolectric Tests
./gradlew testDebugUnitTest
```

## Contributing

Pull requests and device manifest additions are welcome! Please submit an issue first for major architectural changes.

## License

MIT License - see [LICENSE](LICENSE) for details.
