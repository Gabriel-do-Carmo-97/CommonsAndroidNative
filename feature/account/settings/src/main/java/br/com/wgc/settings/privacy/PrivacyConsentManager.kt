package br.com.wgc.settings.privacy

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Categorias granulares de consentimento do titular de dados previstas na LGPD (Lei 13.709/2018) e GDPR.
 */
enum class ConsentCategory(val label: String, val isMandatory: Boolean) {
    /** Cookies e dados estritamente essenciais para funcionamento e segurança do app (sempre ativo). */
    ESSENTIAL("Estritamente Essenciais", isMandatory = true),
    /** Métricas de telemetria, diagnóstico de crashes e análise de performance. */
    ANALYTICS("Telemetria e Diagnóstico", isMandatory = false),
    /** Comunicações de campanhas promocionais, cupons e novidades personalizadas. */
    MARKETING("Comunicações e Marketing", isMandatory = false),
    /** Personalização baseada em localização GPS e histórico de navegação. */
    PERSONALIZATION("Personalização e Geolocalização", isMandatory = false)
}

/**
 * Registro consolidado das preferências de consentimento do usuário.
 *
 * @property essentialAccepted Consentimento essencial (sempre true).
 * @property analyticsAccepted Autorização para coleta de telemetria e métricas de uso.
 * @property marketingAccepted Autorização para envio de e-mails, SMS e notificações de marketing.
 * @property personalizationAccepted Autorização para filtros por proximidade e ofertas locais.
 * @property consentedAtMillis Carimbo de data/hora do aceite em milissegundos.
 * @property policyVersion Versão dos Termos de Uso e Política de Privacidade consentida.
 */
data class PrivacyConsentPreferences(
    val essentialAccepted: Boolean = true,
    val analyticsAccepted: Boolean = false,
    val marketingAccepted: Boolean = false,
    val personalizationAccepted: Boolean = false,
    val consentedAtMillis: Long = System.currentTimeMillis(),
    val policyVersion: String = "1.0"
)

/**
 * Gerenciador corporativo de Consentimento e Privacidade (Conformidade LGPD & GDPR).
 *
 * Gerencia a aceitação granular de coleta de dados do titular, garantindo que telemetria comportamental,
 * geolocalização e campanhas de marketing só sejam ativadas mediante autorização expressa e revogável.
 *
 * ### 📌 Quando Usar:
 * Acione na inicialização do aplicativo para exibir o modal de consentimento LGPD caso o usuário
 * ainda não tenha respondido ou quando os Termos de Privacidade forem versionados.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val consentManager = PrivacyConsentManager(storage)
 *
 * // 1. Verificar se é necessária a revalidação de consentimento:
 * if (consentManager.isConsentRevalidationRequired(currentPolicyVersion = "2.0")) {
 *     exibirModalConsentimentoLGPD()
 * }
 *
 * // 2. Gravar escolhas do usuário:
 * consentManager.saveConsent(
 *     PrivacyConsentPreferences(analyticsAccepted = true, marketingAccepted = false, personalizationAccepted = true)
 * )
 * ```
 *
 * @param storage Armazenamento seguro de preferências persistentes.
 */
@Singleton
class PrivacyConsentManager @Inject constructor(
    private val storage: KeyValueStorage
) {
    companion object {
        private const val KEY_CONSENT_EXISTS = "wgc_lgpd_consent_exists"
        private const val KEY_CONSENT_ANALYTICS = "wgc_lgpd_consent_analytics"
        private const val KEY_CONSENT_MARKETING = "wgc_lgpd_consent_marketing"
        private const val KEY_CONSENT_PERSONALIZATION = "wgc_lgpd_consent_personalization"
        private const val KEY_CONSENT_TIMESTAMP = "wgc_lgpd_consent_time"
        private const val KEY_CONSENT_VERSION = "wgc_lgpd_consent_version"
    }

    /**
     * Salva as preferências granulares de consentimento do titular.
     *
     * @param preferences Objeto com as escolhas aceitas ou recusadas.
     */
    fun saveConsent(preferences: PrivacyConsentPreferences) {
        storage.saveBoolean(KEY_CONSENT_EXISTS, true)
        storage.saveBoolean(KEY_CONSENT_ANALYTICS, preferences.analyticsAccepted)
        storage.saveBoolean(KEY_CONSENT_MARKETING, preferences.marketingAccepted)
        storage.saveBoolean(KEY_CONSENT_PERSONALIZATION, preferences.personalizationAccepted)
        storage.saveLong(KEY_CONSENT_TIMESTAMP, preferences.consentedAtMillis)
        storage.saveString(KEY_CONSENT_VERSION, preferences.policyVersion)
    }

    /**
     * Recupera as preferências de consentimento atualmente ativas.
     *
     * @return [PrivacyConsentPreferences] salvas ou valores padrão caso ainda não registradas.
     */
    fun getConsent(): PrivacyConsentPreferences {
        val exists = storage.getBoolean(KEY_CONSENT_EXISTS, false)
        if (!exists) {
            return PrivacyConsentPreferences(
                essentialAccepted = true,
                analyticsAccepted = false,
                marketingAccepted = false,
                personalizationAccepted = false,
                consentedAtMillis = 0L,
                policyVersion = "none"
            )
        }

        return PrivacyConsentPreferences(
            essentialAccepted = true,
            analyticsAccepted = storage.getBoolean(KEY_CONSENT_ANALYTICS, false),
            marketingAccepted = storage.getBoolean(KEY_CONSENT_MARKETING, false),
            personalizationAccepted = storage.getBoolean(KEY_CONSENT_PERSONALIZATION, false),
            consentedAtMillis = storage.getLong(KEY_CONSENT_TIMESTAMP, 0L),
            policyVersion = storage.getString(KEY_CONSENT_VERSION, "1.0") ?: "1.0"
        )
    }

    /**
     * Verifica se o usuário já registrou suas opções de consentimento ao menos uma vez.
     *
     * @return true se já houver registro gravado.
     */
    fun hasConsented(): Boolean {
        return storage.getBoolean(KEY_CONSENT_EXISTS, false)
    }

    /**
     * Avalia se os Termos de Privacidade foram atualizados e requerem novo aceite do titular.
     *
     * @param currentPolicyVersion Versão vigente da política (ex: "2.0").
     * @return true se a versão salva for divergente da vigente ou se nunca tiver consentido.
     */
    fun isConsentRevalidationRequired(currentPolicyVersion: String): Boolean {
        if (!hasConsented()) return true
        val savedVersion = storage.getString(KEY_CONSENT_VERSION, "") ?: ""
        return savedVersion != currentPolicyVersion
    }

    /**
     * Revoga uma categoria específica de consentimento em conformidade com o Artigo 8º, § 5º da LGPD.
     *
     * @param category Categoria a ser revogada ([ConsentCategory]).
     */
    fun revokeConsentCategory(category: ConsentCategory) {
        if (category.isMandatory) return // Categoria essencial não pode ser revogada isoladamente

        when (category) {
            ConsentCategory.ANALYTICS -> storage.saveBoolean(KEY_CONSENT_ANALYTICS, false)
            ConsentCategory.MARKETING -> storage.saveBoolean(KEY_CONSENT_MARKETING, false)
            ConsentCategory.PERSONALIZATION -> storage.saveBoolean(KEY_CONSENT_PERSONALIZATION, false)
            ConsentCategory.ESSENTIAL -> {}
        }
    }

    /**
     * Remove integralmente todos os registros de consentimento.
     */
    fun clearConsent() {
        storage.remove(KEY_CONSENT_EXISTS)
        storage.remove(KEY_CONSENT_ANALYTICS)
        storage.remove(KEY_CONSENT_MARKETING)
        storage.remove(KEY_CONSENT_PERSONALIZATION)
        storage.remove(KEY_CONSENT_TIMESTAMP)
        storage.remove(KEY_CONSENT_VERSION)
    }
}
