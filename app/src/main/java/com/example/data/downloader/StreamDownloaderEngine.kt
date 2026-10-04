package com.example.data.downloader

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

/**
 * High-performance streaming binary downloader engineered specifically for custom recovery images,
 * kernel binaries, and multi-gigabyte Android ROM ZIP files.
 *
 * Implements:
 * - Scoped Storage compliance (App-specific external directories and Android 10+ MediaStore).
 * - HTTP Range request support for resuming partial downloads.
 * - Exponential Moving Average (EMA) smoothed speed and accurate ETA computation.
 * - Atomic .part file staging to prevent corruption of recovery partitions.
 * - Integrated cryptographic checksum validation (SHA-256 / MD5).
 */
class StreamDownloaderEngine(private val context: Context) {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    private companion object {
        const val BUFFER_SIZE = 64 * 1024 // 64 KB streaming buffer
        const val PROGRESS_INTERVAL_MS = 250L // 4 updates/sec for smooth UI without jitter
        const val SPEED_SMOOTHING_FACTOR = 0.25 // EMA alpha factor for speed calculation
    }

    /**
     * Executes the download stream with real-time updates and checksum verification.
     */
    suspend fun downloadBinary(
        request: DownloadRequest,
        onProgress: (DownloadProgress) -> Unit,
        onStatusChange: (DownloadStatus, String) -> Unit,
        onLog: (String) -> Unit
    ): Result<DownloadUiSnapshot> = withContext(Dispatchers.IO) {
        onLog("[DOWNLOADER] Initializing download request for: ${request.fileName}")
        onLog("[DOWNLOADER] Target URL: ${request.url}")
        onLog("[DOWNLOADER] Storage Target: ${request.destinationType.displayName}")
        onStatusChange(DownloadStatus.CONNECTING, "Resolving server and checking resume support...")

        val stagingDir = getStagingDirectory(request.destinationType)
        if (!stagingDir.exists()) stagingDir.mkdirs()

        val finalFile = File(stagingDir, sanitizeFileName(request.fileName))
        val partFile = File(stagingDir, "${finalFile.name}.part")

        var existingBytes = 0L
        if (partFile.exists()) {
            existingBytes = partFile.length()
            onLog("[DOWNLOADER] Found existing partial download (${DownloadProgress.formatBytes(existingBytes)}). Attempting HTTP Range resume...")
        }

        val requestBuilder = Request.Builder()
            .url(request.url)
            .header("User-Agent", "RecoveryRomStudio/0.8.0 (Android; SystemEngine)")

        if (existingBytes > 0) {
            requestBuilder.header("Range", "bytes=$existingBytes-")
        }

        var speedEma = 0.0
        var lastProgressTime = 0L
        var bytesSinceLastCalc = 0L
        var lastCalcTimestamp = System.currentTimeMillis()

        try {
            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            val code = response.code

            if (!response.isSuccessful && code != 206) {
                // If range request was not acceptable (416), wipe partial file and restart
                if (code == 416) {
                    onLog("[DOWNLOADER] Range not satisfiable. Restarting full download.")
                    partFile.delete()
                    return@withContext downloadBinary(
                        request.copy(id = request.id),
                        onProgress,
                        onStatusChange,
                        onLog
                    )
                }
                throw IOException("Server returned HTTP error code: $code (${response.message})")
            }

            val isPartialContent = (code == 206)
            val responseBody = response.body ?: throw IOException("Empty response body from server")
            val contentLength = responseBody.contentLength()
            val totalBytes = if (isPartialContent) contentLength + existingBytes else contentLength

            onLog("[DOWNLOADER] Connected! HTTP $code. Total content size: ${if (totalBytes > 0) DownloadProgress.formatBytes(totalBytes) else "Chunked/Unknown"}")
            onStatusChange(DownloadStatus.DOWNLOADING, "Streaming binary payload...")

            val appendMode = isPartialContent && existingBytes > 0
            val initialDownloaded = if (appendMode) existingBytes else 0L

            var totalDownloaded = initialDownloaded
            val inputStream: InputStream = responseBody.byteStream()
            val outputStream: OutputStream = FileOutputStream(partFile, appendMode)

            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int

            inputStream.use { input ->
                outputStream.use { output ->
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        coroutineContext.ensureActive()

                        output.write(buffer, 0, bytesRead)
                        totalDownloaded += bytesRead
                        bytesSinceLastCalc += bytesRead

                        val now = System.currentTimeMillis()
                        if (now - lastProgressTime >= PROGRESS_INTERVAL_MS) {
                            val timeDeltaSec = (now - lastCalcTimestamp) / 1000.0
                            if (timeDeltaSec > 0) {
                                val instantSpeed = bytesSinceLastCalc / timeDeltaSec
                                speedEma = if (speedEma == 0.0) instantSpeed else (SPEED_SMOOTHING_FACTOR * instantSpeed + (1 - SPEED_SMOOTHING_FACTOR) * speedEma)
                                bytesSinceLastCalc = 0L
                                lastCalcTimestamp = now
                            }

                            val progressFrac = if (totalBytes > 0) (totalDownloaded.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f) else 0f
                            val remainingBytes = (totalBytes - totalDownloaded).coerceAtLeast(0L)
                            val etaSec = if (speedEma > 1024 && totalBytes > 0) (remainingBytes / speedEma).toLong() else -1L

                            val progressInfo = DownloadProgress(
                                bytesDownloaded = totalDownloaded,
                                totalBytes = totalBytes,
                                progressFraction = progressFrac,
                                speedBytesPerSec = speedEma.toLong(),
                                etaSeconds = etaSec
                            )
                            onProgress(progressInfo)
                            lastProgressTime = now
                        }
                    }
                    output.flush()
                }
            }

            onLog("[DOWNLOADER] Stream completed (${DownloadProgress.formatBytes(totalDownloaded)}). Performing atomic rename...")

            if (finalFile.exists()) finalFile.delete()
            if (!partFile.renameTo(finalFile)) {
                // Fallback copy if atomic rename fails across filesystem boundaries
                partFile.copyTo(finalFile, overwrite = true)
                partFile.delete()
            }

            // Cryptographic Checksum Verification Phase
            var verificationResult: ChecksumVerificationResult? = null
            if (!request.expectedSha256.isNullOrBlank() || !request.expectedMd5.isNullOrBlank()) {
                onStatusChange(DownloadStatus.VERIFYING_CHECKSUM, "Calculating cryptographic hashes...")
                onLog("[CHECKSUM] Initiating multi-hash verification on: ${finalFile.name}")

                val (calculatedMd5, calculatedSha256) = ChecksumVerifier.calculateDualHash(finalFile) { frac ->
                    onProgress(
                        DownloadProgress(
                            bytesDownloaded = totalDownloaded,
                            totalBytes = totalDownloaded,
                            progressFraction = frac,
                            speedBytesPerSec = 0L,
                            etaSeconds = 0L
                        )
                    )
                }

                onLog("[CHECKSUM] Calculated MD5:    $calculatedMd5")
                onLog("[CHECKSUM] Calculated SHA256: $calculatedSha256")

                if (!request.expectedSha256.isNullOrBlank()) {
                    val expected = request.expectedSha256.trim().lowercase()
                    val match = calculatedSha256.equals(expected, ignoreCase = true)
                    verificationResult = ChecksumVerificationResult(
                        algorithm = ChecksumAlgorithm.SHA256,
                        calculatedHash = calculatedSha256,
                        expectedHash = expected,
                        isMatch = match,
                        elapsedMs = 0L,
                        verifiedFile = finalFile
                    )
                    if (!match) {
                        onLog("[WARNING:CHECKSUM] SHA-256 MISMATCH! Image may cause bootloop or brick.")
                        throw IOException("SHA-256 Checksum Mismatch: Expected $expected but computed $calculatedSha256")
                    } else {
                        onLog("[CHECKSUM] SHA-256 Signature MATCHED perfectly! File integrity certified.")
                    }
                } else if (!request.expectedMd5.isNullOrBlank()) {
                    val expected = request.expectedMd5.trim().lowercase()
                    val match = calculatedMd5.equals(expected, ignoreCase = true)
                    verificationResult = ChecksumVerificationResult(
                        algorithm = ChecksumAlgorithm.MD5,
                        calculatedHash = calculatedMd5,
                        expectedHash = expected,
                        isMatch = match,
                        elapsedMs = 0L,
                        verifiedFile = finalFile
                    )
                    if (!match) {
                        onLog("[WARNING:CHECKSUM] MD5 MISMATCH! File corrupted during transit.")
                        throw IOException("MD5 Checksum Mismatch: Expected $expected but computed $calculatedMd5")
                    } else {
                        onLog("[CHECKSUM] MD5 Signature MATCHED perfectly! File integrity certified.")
                    }
                }
            }

            // Export to MediaStore if requested
            var mediaUri: Uri? = null
            if (request.destinationType == DestinationType.PUBLIC_DOWNLOADS_MEDIASTORE) {
                onLog("[STORAGE] Exporting verified package to Android MediaStore Downloads...")
                mediaUri = publishToMediaStore(finalFile)
                if (mediaUri != null) {
                    onLog("[STORAGE] Successfully published to MediaStore: $mediaUri")
                }
            }

            val finalProgress = DownloadProgress(
                bytesDownloaded = totalDownloaded,
                totalBytes = totalDownloaded,
                progressFraction = 1f,
                speedBytesPerSec = 0L,
                etaSeconds = 0L
            )
            onProgress(finalProgress)
            onStatusChange(DownloadStatus.COMPLETED, "Download & verification finished successfully!")
            onLog("[DOWNLOADER] Binary ready at: ${finalFile.absolutePath}")

            Result.success(
                DownloadUiSnapshot(
                    status = DownloadStatus.COMPLETED,
                    activeRequest = request,
                    progress = finalProgress,
                    statusMessage = "Successfully downloaded ${finalFile.name}",
                    checksumResult = verificationResult,
                    savedFile = finalFile,
                    mediaStoreUri = mediaUri,
                    logs = listOf("Finished successfully")
                )
            )

        } catch (e: Exception) {
            onLog("[ERROR] Download aborted: ${e.localizedMessage}")
            onStatusChange(DownloadStatus.FAILED, "Error: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    private fun getStagingDirectory(destinationType: DestinationType): File {
        return when (destinationType) {
            DestinationType.APP_EXTERNAL_STORAGE, DestinationType.PUBLIC_DOWNLOADS_MEDIASTORE -> {
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    ?: File(context.filesDir, "downloads")
            }
            DestinationType.INTERNAL_CACHE -> {
                File(context.cacheDir, "rom_downloads")
            }
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }

    /**
     * Publishes a completed, verified binary file to Android's Public MediaStore Downloads collection.
     */
    private fun publishToMediaStore(sourceFile: File): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, sourceFile.name)
            put(MediaStore.Downloads.MIME_TYPE, getMimeType(sourceFile.name))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/RecoveryRomStudio")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
        }

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        }

        val targetUri = resolver.insert(collectionUri, contentValues) ?: return null

        try {
            resolver.openOutputStream(targetUri)?.use { out ->
                FileInputStream(sourceFile).use { input ->
                    input.copyTo(out)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val finalizeValues = ContentValues().apply {
                    put(MediaStore.Downloads.IS_PENDING, 0)
                }
                resolver.update(targetUri, finalizeValues, null, null)
            }
            return targetUri
        } catch (e: Exception) {
            resolver.delete(targetUri, null, null)
            return null
        }
    }

    private fun getMimeType(fileName: String): String {
        return when {
            fileName.endsWith(".zip", ignoreCase = true) -> "application/zip"
            fileName.endsWith(".img", ignoreCase = true) -> "application/octet-stream"
            fileName.endsWith(".bin", ignoreCase = true) -> "application/octet-stream"
            fileName.endsWith(".tar", ignoreCase = true) -> "application/x-tar"
            fileName.endsWith(".gz", ignoreCase = true) -> "application/gzip"
            else -> "application/octet-stream"
        }
    }
}
