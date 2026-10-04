#!/usr/bin/env bash
set -e

TAG_NAME="${1:-v0.8.0}"
RELEASE_TITLE="Recovery & ROM Studio ${TAG_NAME} Public Demo"
APK_SOURCE="app/build/outputs/apk/debug/app-debug.apk"

echo "=== Building APK for ${TAG_NAME} ==="
gradle :app:assembleDebug

if [ ! -f "$APK_SOURCE" ]; then
    echo "Error: APK build failed or APK not found at $APK_SOURCE"
    exit 1
fi

echo "=== Packaging APK Assets ==="
mkdir -p apk_build
cp "$APK_SOURCE" "apk_build/recovery_rom_builder_${TAG_NAME}.apk"
cp "$APK_SOURCE" "apk_build/recovery_rom_builder.apk"
cp "$APK_SOURCE" "apk_build/app-debug.apk"

echo "=== Creating Tag and Pushing to GitHub ==="
git tag -a "$TAG_NAME" -m "$RELEASE_TITLE" || true

echo "Pushing tag to GitHub (if remote configured)..."
git push origin "$TAG_NAME" || echo "Note: Push to your GitHub repo to automatically trigger the Release workflow!"

echo "=== Release workflow file configured in .github/workflows/release.yml ==="
echo "When pushed, GitHub Actions will automatically create the Release and upload the APK files."
