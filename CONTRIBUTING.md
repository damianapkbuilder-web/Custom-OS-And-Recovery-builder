# Contributing to Recovery & Custom ROM Packager

Thank you for contributing to the Custom ROM & Recovery Packager!

## Guidelines

1. **Fork and Branch**: Create a feature branch from `main`.
2. **Device Profiles**: When adding new devices to `DeviceRepository`, verify hardware codenames, partition layouts, and architecture flags.
3. **Root Modules**: Ensure any added root or patch installer maintains AOSP compatibility and does not interfere with system-as-root partitions.
4. **Code Quality**: Follow standard Kotlin style guidelines and Jetpack Compose best practices.
5. **Testing**: Run all unit tests before creating a pull request (`gradle :app:testDebugUnitTest`).
