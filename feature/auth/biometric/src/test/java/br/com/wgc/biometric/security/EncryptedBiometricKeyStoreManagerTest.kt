package br.com.wgc.biometric.security

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Testes unitários para [EncryptedBiometricKeyStoreManager] validando cifragem,
 * decifragem e checagem de estado do Keystore com biometria.
 */
class EncryptedBiometricKeyStoreManagerTest {

    private lateinit var manager: EncryptedBiometricKeyStoreManager

    @Before
    fun setup() {
        manager = EncryptedBiometricKeyStoreManager()
    }

    @Test
    fun `encrypt and decrypt roundtrip restores original payload when biometrically authenticated`() {
        val original = "session_token_xyz_12345".toByteArray()

        val encryptResult = manager.encryptSensitivePayload(original, isBiometricallyAuthenticated = true)
        assertTrue(encryptResult.isSuccess)
        assertFalse(encryptResult.resultData.contentEquals(original))

        val decryptResult = manager.decryptSensitivePayload(encryptResult.resultData!!, isBiometricallyAuthenticated = true)
        assertTrue(decryptResult.isSuccess)
        assertArrayEquals(original, decryptResult.resultData)
    }

    @Test
    fun `encrypt fails when biometric authentication was not provided`() {
        val original = "sensitive_data".toByteArray()

        val result = manager.encryptSensitivePayload(original, isBiometricallyAuthenticated = false)
        assertFalse(result.isSuccess)
        assertTrue(result.errorMessage?.contains("obrigatória") == true)
    }

    @Test
    fun `encrypt fails when keystore key is not ready or invalidated`() {
        manager.setKeyStatus(KeyStoreStatus.INVALIDATED_BY_BIOMETRIC_ENROLLMENT)

        assertTrue(manager.isKeyPermanentlyInvalidated())

        val result = manager.encryptSensitivePayload("test".toByteArray(), isBiometricallyAuthenticated = true)
        assertFalse(result.isSuccess)
        assertTrue(result.errorMessage?.contains("não se encontra") == true)
    }
}
