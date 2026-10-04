package com.example

import com.example.data.downloader.ChecksumAlgorithm
import com.example.data.downloader.ChecksumVerifier
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ChecksumVerifierTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun testChecksumCalculationAndVerification() = runBlocking {
        val testFile = tempFolder.newFile("recovery_test.img")
        testFile.writeText("RECOVERY_PAYLOAD_BINARY_TEST_DATA_12345")

        // Expected SHA-256 for "RECOVERY_PAYLOAD_BINARY_TEST_DATA_12345"
        val calculatedSha256 = ChecksumVerifier.calculateHash(testFile, ChecksumAlgorithm.SHA256)
        assertTrue(calculatedSha256.isNotBlank())
        assertEquals(64, calculatedSha256.length)

        val result = ChecksumVerifier.verifyFile(
            file = testFile,
            expectedHash = calculatedSha256,
            algorithm = ChecksumAlgorithm.SHA256
        )
        assertTrue(result.isMatch)
        assertEquals(calculatedSha256, result.calculatedHash)
    }

    @Test
    fun testDualHashCalculation() = runBlocking {
        val testFile = tempFolder.newFile("flashable_rom_test.zip")
        testFile.writeBytes(ByteArray(1024 * 128) { it.toByte() })

        val (md5, sha256) = ChecksumVerifier.calculateDualHash(testFile)
        assertEquals(32, md5.length)
        assertEquals(64, sha256.length)
    }
}
