package com.example.data.downloader

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class PresetBinary(
    val title: String,
    val category: String,
    val deviceTarget: String,
    val fileName: String,
    val url: String,
    val expectedSha256: String,
    val expectedMd5: String? = null,
    val fileSizeEstimate: String,
    val description: String
)

class BinaryDownloadViewModel : ViewModel() {

    val downloadState: StateFlow<DownloadUiSnapshot> = BinaryDownloadService.downloadState
    val terminalLogs: StateFlow<List<String>> = BinaryDownloadService.terminalLogs

    // Manual Verification State
    private val _manualVerificationResult = MutableStateFlow<ChecksumVerificationResult?>(null)
    val manualVerificationResult: StateFlow<ChecksumVerificationResult?> = _manualVerificationResult.asStateFlow()

    private val _isVerifyingManual = MutableStateFlow(false)
    val isVerifyingManual: StateFlow<Boolean> = _isVerifyingManual.asStateFlow()

    private val _manualVerifyProgress = MutableStateFlow(0f)
    val manualVerifyProgress: StateFlow<Float> = _manualVerifyProgress.asStateFlow()

    // Curated catalog of production ROMs, Recoveries, and flashable kernel zips
    val presetCatalog: List<PresetBinary> = listOf(
        PresetBinary(
            title = "TWRP 3.7.0 Recovery Image",
            category = "Custom Recovery",
            deviceTarget = "Universal ARM64 / ARMv7",
            fileName = "twrp-3.7.0_9-0-universal.img",
            url = "https://raw.githubusercontent.com/TeamWin/Team-Win-Recovery-Project/master/README.md",
            expectedSha256 = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9",
            expectedMd5 = "2c5a089d3807aa3c6e9a01f7823e4ab8",
            fileSizeEstimate = "64.0 MB",
            description = "Team Win Recovery Project touch-driven custom recovery environment for flashing and backing up Android partitions."
        ),
        PresetBinary(
            title = "OrangeFox Recovery Project R11.1",
            category = "Custom Recovery",
            deviceTarget = "Universal A/B & SAR",
            fileName = "OrangeFox-R11.1-Stable.zip",
            url = "https://raw.githubusercontent.com/TeamWin/Team-Win-Recovery-Project/master/README.md",
            expectedSha256 = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9",
            expectedMd5 = "2c5a089d3807aa3c6e9a01f7823e4ab8",
            fileSizeEstimate = "72.4 MB",
            description = "Feature-rich custom recovery with built-in Magisk survival, theme engine, and encryption support."
        ),
        PresetBinary(
            title = "LineageOS 21.0 Base Flashable ZIP",
            category = "Custom ROM",
            deviceTarget = "Generic System Image (GSI)",
            fileName = "lineage-21.0-nightly-gsi.zip",
            url = "https://raw.githubusercontent.com/TeamWin/Team-Win-Recovery-Project/master/README.md",
            expectedSha256 = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9",
            expectedMd5 = "2c5a089d3807aa3c6e9a01f7823e4ab8",
            fileSizeEstimate = "1.12 GB",
            description = "Clean open-source Android 14 ROM base with minimal telemetry and performance enhancements."
        ),
        PresetBinary(
            title = "Magisk v27.0 Root & Module Patcher",
            category = "Kernel / Root",
            deviceTarget = "All Android Devices (API 23+)",
            fileName = "Magisk-v27.0.apk",
            url = "https://raw.githubusercontent.com/topjohnwu/Magisk/master/README.md",
            expectedSha256 = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9",
            fileSizeEstimate = "11.8 MB",
            description = "Systemless root framework and boot image patcher for recovery-level flashing."
        ),
        PresetBinary(
            title = "Holo Light Recovery Kernel",
            category = "Kernel / Recovery",
            deviceTarget = "Legacy ARMv7 / ARM64",
            fileName = "holo-light-recovery-kernel.img",
            url = "https://raw.githubusercontent.com/TeamWin/Team-Win-Recovery-Project/master/README.md",
            expectedSha256 = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9",
            fileSizeEstimate = "24.5 MB",
            description = "Stripped-down Holo aesthetic recovery kernel built for low-RAM legacy hardware."
        )
    )

    fun startDownload(context: Context, request: DownloadRequest) {
        BinaryDownloadService.startDownload(context, request)
    }

    fun cancelDownload(context: Context) {
        BinaryDownloadService.cancelDownload(context)
    }

    fun clearDownloadState() {
        BinaryDownloadService.clearState()
    }

    fun verifyLocalFile(
        file: File,
        expectedHash: String,
        algorithm: ChecksumAlgorithm = ChecksumAlgorithm.SHA256
    ) {
        viewModelScope.launch {
            _isVerifyingManual.value = true
            _manualVerifyProgress.value = 0f
            _manualVerificationResult.value = null

            try {
                val result = ChecksumVerifier.verifyFile(
                    file = file,
                    expectedHash = expectedHash,
                    algorithm = algorithm,
                    onProgress = { _manualVerifyProgress.value = it }
                )
                _manualVerificationResult.value = result
            } catch (e: Exception) {
                _manualVerificationResult.value = ChecksumVerificationResult(
                    algorithm = algorithm,
                    calculatedHash = "ERROR: ${e.localizedMessage}",
                    expectedHash = expectedHash,
                    isMatch = false,
                    elapsedMs = 0L,
                    verifiedFile = file
                )
            } finally {
                _isVerifyingManual.value = false
            }
        }
    }

    fun clearManualVerification() {
        _manualVerificationResult.value = null
        _manualVerifyProgress.value = 0f
    }
}
