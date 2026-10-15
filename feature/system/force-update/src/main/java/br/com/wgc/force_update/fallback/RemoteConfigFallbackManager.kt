package br.com.wgc.force_update.fallback

import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Representação estruturada das configurações locais de contingência.
 *
 * @property minRequiredAppVersion Código da versão mínima exigida.
 * @property forceUpdateEnabled Indica se o bloqueio de tela está ativo.
 * @property storeUrl Link padrão da Google Play Store.
 * @property offlineSyncIntervalMinutes Intervalo periódico de sincronização.
 * @property isMaintenanceMode Indica se os servidores estão em manutenção.
 */
data class FallbackConfiguration(
    val minRequiredAppVersion: Long,
    val forceUpdateEnabled: Boolean,
    val storeUrl: String,
    val offlineSyncIntervalMinutes: Int,
    val isMaintenanceMode: Boolean
)

/**
 * Gestor corporativo de fallback de configurações remotas para inicialização offline resiliente.
 *
 * Carrega e analisa o arquivo JSON estático empacotado nos assets da aplicação caso o aplicativo
 * seja aberto pela primeira vez em áreas sem cobertura de internet ou com indisponibilidade do OmniBackend.
 *
 * ### 📌 Quando Usar:
 * - Durante a inicialização do app antes de realizar chamadas de rede.
 * - Como fonte primária de valores padrão do Remote Config (`RemoteConfigRepository.setDefaultsAsync`).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val fallbackManager = RemoteConfigFallbackManager()
 * val config = fallbackManager.parseFallbackJson(jsonContent)
 * println("Versão mínima padrão: ${config.minRequiredAppVersion}")
 * ```
 */
@Singleton
class RemoteConfigFallbackManager @Inject constructor() {

    companion object {
        const val DEFAULT_FALLBACK_JSON = """
        {
          "min_required_app_version": 1,
          "force_update_enabled": false,
          "store_url": "https://play.google.com/store/apps/details?id=br.com.wgc.commonsandroidnative",
          "offline_sync_interval_minutes": 15,
          "maintenance_mode": false
        }
        """
    }

    /**
     * Realiza o parseamento seguro de um texto JSON para [FallbackConfiguration].
     *
     * @param jsonText Conteúdo bruto do JSON extraído dos assets ou cache local.
     * @return [FallbackConfiguration] contendo os valores extraídos ou defaults em caso de erro de parse.
     */
    fun parseFallbackJson(jsonText: String?): FallbackConfiguration {
        val effectiveJson = jsonText?.takeIf { it.isNotBlank() } ?: DEFAULT_FALLBACK_JSON

        return try {
            val jsonObject = JSONObject(effectiveJson)
            FallbackConfiguration(
                minRequiredAppVersion = jsonObject.optLong("min_required_app_version", 1L),
                forceUpdateEnabled = jsonObject.optBoolean("force_update_enabled", false),
                storeUrl = jsonObject.optString(
                    "store_url",
                    "https://play.google.com/store/apps/details?id=br.com.wgc.commonsandroidnative"
                ),
                offlineSyncIntervalMinutes = jsonObject.optInt("offline_sync_interval_minutes", 15),
                isMaintenanceMode = jsonObject.optBoolean("maintenance_mode", false)
            )
        } catch (_: Exception) {
            FallbackConfiguration(
                minRequiredAppVersion = 1L,
                forceUpdateEnabled = false,
                storeUrl = "https://play.google.com/store/apps/details?id=br.com.wgc.commonsandroidnative",
                offlineSyncIntervalMinutes = 15,
                isMaintenanceMode = false
            )
        }
    }
}
