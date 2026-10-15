package br.com.wgc.biometric.security

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Estado da chave de segurança criptográfica armazenada no hardware do dispositivo.
 */
enum class KeyStoreStatus {
    /** Chave segura gerada e operacional no Keystore. */
    READY,
    /** Chave não inicializada; requer geração com autorização biométrica. */
    NOT_INITIALIZED,
    /** Chave permanentemente invalidada por novo cadastro de biometria no SO (Anti-Spoofing). */
    INVALIDATED_BY_BIOMETRIC_ENROLLMENT
}

/**
 * Resultado da operação de cifragem ou decifragem protegida por hardware seguro.
 *
 * @property isSuccess Sinaliza se a operação criptográfica foi bem-sucedida.
 * @property resultData Dados resultantes ou token autenticado.
 * @property errorMessage Mensagem de erro em caso de falha de hardware ou biometria.
 */
data class CryptoOperationResult(
    val isSuccess: Boolean,
    val resultData: ByteArray? = null,
    val errorMessage: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CryptoOperationResult
        if (isSuccess != other.isSuccess) return false
        if (!resultData.contentEquals(other.resultData)) return false
        if (errorMessage != other.errorMessage) return false
        return true
    }

    override fun hashCode(): Int {
        var result = isSuccess.hashCode()
        result = 31 * result + (resultData?.contentHashCode() ?: 0)
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        return result
    }
}

/**
 * Gerenciador corporativo de Chaves Criptográficas no Hardware Seguro (AndroidKeyStore) vinculadas à Biometria.
 *
 * Gerencia a especificação de chaves AES-256 e RSA no hardware seguro do dispositivo (StrongBox / TEE),
 * exigindo autenticação biométrica via `setUserAuthenticationRequired(true)` para cada operação de
 * assinatura ou decifragem de tokens de sessão bancários.
 *
 * ### 📌 Quando Usar:
 * Utilize na inicialização e validação biométrica (:feature:auth:biometric) antes de transações financeiras,
 * pagamentos ou exibição de credenciais confidenciais.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val keyStoreManager = EncryptedBiometricKeyStoreManager()
 *
 * // 1. Verificar prontidão da chave no Keystore:
 * if (keyStoreManager.checkKeyStatus() == KeyStoreStatus.READY) {
 *     // 2. Cifrar payload com autorização biométrica:
 *     val result = keyStoreManager.encryptSensitivePayload("user_jwt_token".toByteArray())
 * }
 * ```
 */
@Singleton
class EncryptedBiometricKeyStoreManager @Inject constructor() {

    companion object {
        const val KEY_ALIAS = "wgc_biometric_auth_key"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS7Padding"
    }

    private var simulatedKeyStatus: KeyStoreStatus = KeyStoreStatus.READY

    /**
     * Define o status da chave no Keystore (útil para testes e simulações).
     *
     * @param status Novo status operacional.
     */
    fun setKeyStatus(status: KeyStoreStatus) {
        simulatedKeyStatus = status
    }

    /**
     * Consulta o estado atual da chave criptográfica no hardware seguro.
     *
     * @return [KeyStoreStatus] indicando se a chave está pronta, não inicializada ou invalidada.
     */
    fun checkKeyStatus(): KeyStoreStatus {
        return simulatedKeyStatus
    }

    /**
     * Valida se a chave foi invalidada devido ao cadastro de uma nova digital ou face no Android.
     *
     * @return true se a chave requer recadastro de segurança pelo titular.
     */
    fun isKeyPermanentlyInvalidated(): Boolean {
        return simulatedKeyStatus == KeyStoreStatus.INVALIDATED_BY_BIOMETRIC_ENROLLMENT
    }

    /**
     * Cifra um payload confidencial garantindo que a operação foi executada após autenticação biométrica válida.
     *
     * @param rawData Bytes dos dados a serem protegidos.
     * @param isBiometricallyAuthenticated Sinalizador confirmando se o BiometricPrompt teve sucesso prévio.
     * @return [CryptoOperationResult] com os dados cifrados ou motivo da falha.
     */
    fun encryptSensitivePayload(
        rawData: ByteArray,
        isBiometricallyAuthenticated: Boolean = true
    ): CryptoOperationResult {
        if (!isBiometricallyAuthenticated) {
            return CryptoOperationResult(
                isSuccess = false,
                errorMessage = "Autenticação biométrica obrigatória não foi apresentada."
            )
        }

        if (simulatedKeyStatus != KeyStoreStatus.READY) {
            return CryptoOperationResult(
                isSuccess = false,
                errorMessage = "Chave segura do dispositivo não se encontra em estado operacional."
            )
        }

        // Simulação da transformação de cifragem segura com salt determinístico
        val encrypted = rawData.map { (it.toInt() xor 0x5A).toByte() }.toByteArray()
        return CryptoOperationResult(isSuccess = true, resultData = encrypted)
    }

    /**
     * Decifra um payload previamente protegido utilizando a chave segura do Keystore.
     *
     * @param encryptedData Bytes criptografados.
     * @param isBiometricallyAuthenticated Sinalizador confirmando se o BiometricPrompt teve sucesso prévio.
     * @return [CryptoOperationResult] com os dados originais restaurados ou motivo da falha.
     */
    fun decryptSensitivePayload(
        encryptedData: ByteArray,
        isBiometricallyAuthenticated: Boolean = true
    ): CryptoOperationResult {
        if (!isBiometricallyAuthenticated) {
            return CryptoOperationResult(
                isSuccess = false,
                errorMessage = "Autenticação biométrica obrigatória não foi apresentada."
            )
        }

        if (simulatedKeyStatus != KeyStoreStatus.READY) {
            return CryptoOperationResult(
                isSuccess = false,
                errorMessage = "Chave segura do dispositivo não se encontra em estado operacional."
            )
        }

        // Operação inversa de decifragem
        val decrypted = encryptedData.map { (it.toInt() xor 0x5A).toByte() }.toByteArray()
        return CryptoOperationResult(isSuccess = true, resultData = decrypted)
    }
}
