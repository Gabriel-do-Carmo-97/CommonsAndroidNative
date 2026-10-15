package br.com.wgc.commonsandroidnative.network

import br.com.wgc.core.network.security.DynamicCertificatePinner
import okhttp3.CertificatePinner
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Configurador corporativo de segurança de transporte SSL/TLS com Certificate Pinning dinâmico.
 *
 * Utiliza o [DynamicCertificatePinner] do CoreAndroidNative 1.5.1 para registrar os hashes
 * públicos SHA-256 dos certificados TLS dos servidores de produção, prevenindo ataques do tipo
 * Man-in-the-Middle (MitM) mesmo se Autoridades Certificadoras (CAs) do sistema forem comprometidas.
 *
 * Suporta rotação dinâmica de chaves sem necessidade de atualização forçada na loja.
 *
 * ### 📌 Quando Usar:
 * Utilize na inicialização da camada de rede e na configuração da instância principal do
 * OkHttpClient para assegurar integridade nas comunicações com APIs corporativas e OmniBackend.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Inject
 * lateinit var networkSecurityConfig: NetworkSecurityConfig
 *
 * val okHttpClient = OkHttpClient.Builder()
 *     .certificatePinner(networkSecurityConfig.buildCertificatePinner())
 *     .build()
 * ```
 *
 * @property dynamicCertificatePinner Instância gerenciadora de pins TLS do Core.
 */
@Singleton
class NetworkSecurityConfig @Inject constructor(
    private val dynamicCertificatePinner: DynamicCertificatePinner
) {

    init {
        registerDefaultCorporatePins()
    }

    /**
     * Registra os hashes SHA-256 corporativos padrão para os domínios de produção.
     *
     * ### 📌 Quando Usar:
     * Invocado na inicialização para proteger as chamadas para APIs corporativas,
     * Firestore e serviços de identidade Google/Firebase.
     */
    private fun registerDefaultCorporatePins() {
        // Pinos padrão do ecossistema WGC e Firebase/Google APIs
        dynamicCertificatePinner.addPins(
            "*.wgc.com.br",
            "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="
        )
        dynamicCertificatePinner.addPins(
            "firestore.googleapis.com",
            "sha256/r/mIts6OE1MpXkeGOPDdT4M7ZsLpttggUnNdHl+Glb0=",
            "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18="
        )
        dynamicCertificatePinner.addPins(
            "identitytoolkit.googleapis.com",
            "sha256/r/mIts6OE1MpXkeGOPDdT4M7ZsLpttggUnNdHl+Glb0=",
            "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18="
        )
    }

    /**
     * Adiciona hashes adicionais em tempo de execução para um domínio específico.
     *
     * ### 📌 Quando Usar:
     * Útil quando novas chaves são distribuídas via Remote Config do OmniBackend
     * antes da expiração de certificados antigos.
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * networkSecurityConfig.addPins("api.parceiro.com.br", "sha256/...", "sha256/...")
     * ```
     *
     * @param pattern Padrão de host ou domínio wildcard (ex: `api.empresa.com.br`).
     * @param pins Hashes SHA-256 codificados em Base64 prefixados com `sha256/`.
     */
    fun addPins(pattern: String, vararg pins: String) {
        dynamicCertificatePinner.addPins(pattern, *pins)
    }

    /**
     * Compila e retorna a instância imutável do [CertificatePinner] do OkHttp.
     *
     * ### 📌 Quando Usar:
     * Invocado durante o build do cliente HTTP ou na reconfiguração dinâmica do socket factory.
     *
     * ### 🛠️ Modo de Usar:
     * ```kotlin
     * val pinner = networkSecurityConfig.buildCertificatePinner()
     * ```
     *
     * @return [CertificatePinner] configurado com todos os domínios e hashes registrados.
     */
    fun buildCertificatePinner(): CertificatePinner {
        return dynamicCertificatePinner.buildCertificatePinner()
    }
}
