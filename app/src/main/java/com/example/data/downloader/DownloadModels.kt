package com.example.data.downloader

import android.net.Uri
import java.io.File
import java.util.Locale

/**
 * Storage destinations supported under modern Android Scoped Storage rules.
 */
enum class DestinationType(val displayName: String, val description: String) {
    APP_EXTERNAL_STORAGE(
        displayName = "App External Storage",
        description = "Isolated app directory (Android/data/com.example/files/Download). Zero runtime permissions required."
    ),
    PUBLIC_DOWNLOADS_MEDIASTORE(
        displayName = "Public Downloads (MediaStore)",
        description = "Public Downloads folder via Android MediaStore API. Accessible by recovery flasher tools and file managers."
    ),
    INTERNAL_CACHE(
        displayName = "Internal Cache",
        description = "Private cache partition for temporary staging before zip repacking."
    )
}

/**
 * Supported cryptographic hash algorithms for verifying flashable zip and img integrity.
 */
enum class ChecksumAlgorithm(val standardName: String) {
    SHA256("SHA-256"),
    MD5("MD5"),
    SHA1("SHA-1")
}

/**
 * Real-time download progress model exposed to ViewModel and Compose StateFlow.
 */
data class DownloadProgress(
    val bytesDownloaded: Long = 0L,
    val totalBytes: Long = 0L,
    val progressFraction: Float = 0f,
    val speedBytesPerSec: Long = 0L,
    val etaSeconds: Long = -1L
) {
    val percentage: Int
        get() = (progressFraction * 100f).toInt().coerceIn(0, 100)

    val formattedSpeed: String
        get() = when {
            speedBytesPerSec <= 0 -> "0 KB/s"
            speedBytesPerSec < 1024 * 1024 -> String.format(Locale.US, "%.1f KB/s", speedBytesPerSec / 1024.0)
            else -> String.format(Locale.US, "%.2f MB/s", speedBytesPerSec / (1024.0 * 1024.0))
        }

    val formattedDownloaded: String
        get() = formatBytes(bytesDownloaded)

    val formattedTotal: String
        get() = if (totalBytes > 0) formatBytes(totalBytes) else "Unknown"

    val formattedEta: String
        get() = when {
            etaSeconds < 0 -> "--"
            etaSeconds < 60 -> "${etaSeconds}s"
            etaSeconds < 3600 -> "${etaSeconds / 60}m ${etaSeconds % 60}s"
            else -> "${etaSeconds / 3600}h ${(etaSeconds % 3600) / 60}m"
        }

    companion object {
        fun formatBytes(bytes: Long): String {
            if (bytes <= 0) return "0 B"
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format(Locale.US, "%.2f GB", gb)
                mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
                kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
                else -> "$bytes B"
            }
        }
    }
}

/**
 * Lifecycle status of an active or completed binary download.
 */
enum class DownloadStatus {
    IDLE,
    CONNECTING,
    DOWNLOADING,
    PAUSED,
    VERIFYING_CHECKSUM,
    COMPLETED,
    FAILED,
    CANCELLED
}

/**
 * Result of post-download cryptographic verification.
 */
data class ChecksumVerificationResult(
    val algorithm: ChecksumAlgorithm,
    val calculatedHash: String,
    val expectedHash: String,
    val isMatch: Boolean,
    val elapsedMs: Long,
    val verifiedFile: File
)

/**
 * Request descriptor for downloading a binary recovery or ROM package.
 */
data class DownloadRequest(
    val id: String = java.util.UUID.randomUUID().toString(),
    val url: String,
    val fileName: String,
    val destinationType: DestinationType = DestinationType.APP_EXTERNAL_STORAGE,
    val expectedMd5: String? = null,
    val expectedSha256: String? = null,
    val category: String = "Custom Recovery",
    val deviceTarget: String = "Universal",
    val description: String = ""
)

/**
 * Complete UI state snapshot for binary downloader.
 */
data class DownloadUiSnapshot(
    val status: DownloadStatus = DownloadStatus.IDLE,
    val activeRequest: DownloadRequest? = null,
    val progress: DownloadProgress = DownloadProgress(),
    val statusMessage: String = "Ready to download",
    val checksumResult: ChecksumVerificationResult? = null,
    val error: String? = null,
    val savedFile: File? = null,
    val mediaStoreUri: Uri? = null,
    val logs: List<String> = emptyList()
)
