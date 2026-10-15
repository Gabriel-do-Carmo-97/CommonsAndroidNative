package br.com.wgc.authentication.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [RootSecurityDetector] validando detecção de binários 'su', Magisk e test-keys.
 */
class RootSecurityDetectorTest {

    private lateinit var detector: RootSecurityDetector

    @Before
    fun setup() {
        detector = RootSecurityDetector()
    }

    @Test
    fun `checkDeviceIntegrity reports SECURE when no threat exists`() {
        val integrity = detector.checkDeviceIntegrity(
            fileChecker = { false },
            buildTags = "release-keys"
        )

        assertFalse(integrity.isCompromised)
        assertFalse(integrity.shouldBlockFinancialOperations)
        assertTrue(integrity.detectedThreats.isEmpty())
    }

    @Test
    fun `checkDeviceIntegrity detects su binary and blocks financial operations`() {
        val integrity = detector.checkDeviceIntegrity(
            fileChecker = { path -> path == "/system/xbin/su" },
            buildTags = "release-keys"
        )

        assertTrue(integrity.isCompromised)
        assertTrue(integrity.shouldBlockFinancialOperations)
        assertEquals(1, integrity.detectedThreats.size)
        assertEquals(RootDetectionReason.SU_BINARY_FOUND, integrity.detectedThreats[0])
    }

    @Test
    fun `checkDeviceIntegrity detects test-keys build tags`() {
        val integrity = detector.checkDeviceIntegrity(
            fileChecker = { false },
            buildTags = "dev-keys,test-keys"
        )

        assertTrue(integrity.isCompromised)
        assertEquals(RootDetectionReason.TEST_KEYS_BUILD, integrity.detectedThreats[0])
    }

    @Test
    fun `checkDeviceIntegrity detects multiple threats concurrently`() {
        val integrity = detector.checkDeviceIntegrity(
            fileChecker = { path -> path == "/sbin/su" || path == "/data/adb/magisk" },
            buildTags = "test-keys"
        )

        assertTrue(integrity.isCompromised)
        assertEquals(3, integrity.detectedThreats.size)
        assertTrue(integrity.detectedThreats.contains(RootDetectionReason.SU_BINARY_FOUND))
        assertTrue(integrity.detectedThreats.contains(RootDetectionReason.MAGISK_DETECTED))
        assertTrue(integrity.detectedThreats.contains(RootDetectionReason.TEST_KEYS_BUILD))
    }
}
