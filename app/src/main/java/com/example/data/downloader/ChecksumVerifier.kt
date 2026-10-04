package com.example.data.downloader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import kotlin.coroutines.coroutineContext

/**
 * High-performance cryptographic checksum verifier for large ROMs, recoveries, and system images.
 * Streams data in chunks (64 KB) to avoid OutOfMemory errors and calculates hashes with real-time progress.
 */
object ChecksumVerifier {

    private const val BUFFER_SIZE = 64 * 1024 // 64 KB chunk size

    /**
     * Verifies file integrity against an expected hash using the specified algorithm.
     */
    suspend fun verifyFile(
        file: File,
        expectedHash: String,
        algorithm: ChecksumAlgorithm = ChecksumAlgorithm.SHA256,
        onProgress: ((Float) -> Unit)? = null
    ): ChecksumVerificationResult = withContext(Dispatchers.IO) {
        require(file.exists()) { "Target binary file does not exist: ${file.absolutePath}" }
        val startTime = System.currentTimeMillis()
        val calculated = calculateHash(file, algorithm, onProgress)
        val cleanExpected = expectedHash.trim().lowercase()
        val cleanCalculated = calculated.trim().lowercase()
        val isMatch = cleanCalculated == cleanExpected
        val elapsedMs = System.currentTimeMillis() - startTime

        ChecksumVerificationResult(
            algorithm = algorithm,
            calculatedHash = cleanCalculated,
            expectedHash = cleanExpected,
            isMatch = isMatch,
            elapsedMs = elapsedMs,
            verifiedFile = file
        )
    }

    /**
     * Calculates cryptographic hash in a memory-safe stream.
     */
    suspend fun calculateHash(
        file: File,
        algorithm: ChecksumAlgorithm = ChecksumAlgorithm.SHA256,
        onProgress: ((Float) -> Unit)? = null
    ): String = withContext(Dispatchers.IO) {
        val digest = MessageDigest.getInstance(algorithm.standardName)
        val totalBytes = file.length()
        var readBytes = 0L

        FileInputStream(file).use { fis ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                coroutineContext.ensureActive()
                digest.update(buffer, 0, bytesRead)
                readBytes += bytesRead
                if (totalBytes > 0 && onProgress != null) {
                    val frac = (readBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                    onProgress(frac)
                }
            }
        }

        bytesToHex(digest.digest())
    }

    /**
     * Computes both MD5 and SHA-256 simultaneously in a single pass over the file.
     */
    suspend fun calculateDualHash(
        file: File,
        onProgress: ((Float) -> Unit)? = null
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        val md5Digest = MessageDigest.getInstance("MD5")
        val sha256Digest = MessageDigest.getInstance("SHA-256")
        val totalBytes = file.length()
        var readBytes = 0L

        FileInputStream(file).use { fis ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                coroutineContext.ensureActive()
                md5Digest.update(buffer, 0, bytesRead)
                sha256Digest.update(buffer, 0, bytesRead)
                readBytes += bytesRead
                if (totalBytes > 0 && onProgress != null) {
                    val frac = (readBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                    onProgress(frac)
                }
            }
        }

        Pair(bytesToHex(md5Digest.digest()), bytesToHex(sha256Digest.digest()))
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        val hexArray = "0123456789abcdef".toCharArray()
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = hexArray[v ushr 4]
            hexChars[i * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}
