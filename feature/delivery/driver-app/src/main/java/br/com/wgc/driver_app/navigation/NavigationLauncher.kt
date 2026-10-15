package br.com.wgc.driver_app.navigation

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Aplicativos externos de GPS e navegação curva-a-curva suportados.
 */
enum class ExternalNavigationApp {
    /** Google Maps oficial. */
    GOOGLE_MAPS,
    /** Waze Social GPS & Trânsito. */
    WAZE,
    /** Seletor genérico do sistema Android (geo: intent). */
    SYSTEM_DEFAULT
}

/**
 * Especificação corporativa da Intent Android para abertura de GPS externo.
 *
 * @property action Ação da Intent (ex: `android.intent.action.VIEW`).
 * @property uriString URI formatada compatível com o aplicativo de navegação alvo.
 * @property targetPackage Pacote Android explícito (ex: `com.google.android.apps.maps`) ou null para chooser.
 */
data class NavigationIntentSpec(
    val action: String,
    val uriString: String,
    val targetPackage: String?
)

/**
 * Gerenciador e construtor de Intents de Navegação Curva-a-Curva Externa (Google Maps & Waze).
 *
 * Gera URIs canônicas suportadas pelas APIs do Google Maps e Waze, permitindo ao entregador
 * iniciar rotas de navegação direta com um toque a partir da tela da corrida.
 *
 * ### 📌 Quando Usar:
 * Utilize na tela de entrega do condutor ([br.com.wgc.driver_app.screen.DriverAppScreen]) nos botões
 * "Navegar com Google Maps" ou "Navegar com Waze" para despachar o percurso turn-by-turn.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val launcher = NavigationLauncher()
 *
 * // 1. Construir especificação para Google Maps:
 * val gMapsSpec = launcher.buildNavigationSpec(
 *     app = ExternalNavigationApp.GOOGLE_MAPS,
 *     destLatitude = -23.5505,
 *     destLongitude = -46.6333,
 *     destinationLabel = "Rua das Flores, 120"
 * )
 *
 * // 2. Lançar no Android:
 * val intent = Intent(gMapsSpec.action, Uri.parse(gMapsSpec.uriString)).apply {
 *     gMapsSpec.targetPackage?.let { setPackage(it) }
 * }
 * context.startActivity(intent)
 * ```
 */
@Singleton
class NavigationLauncher @Inject constructor() {

    companion object {
        const val ACTION_VIEW = "android.intent.action.VIEW"
        const val PACKAGE_GOOGLE_MAPS = "com.google.android.apps.maps"
        const val PACKAGE_WAZE = "com.waze"
    }

    /**
     * Constrói a especificação [NavigationIntentSpec] para o aplicativo selecionado.
     *
     * @param app Aplicativo de navegação desejado ([ExternalNavigationApp]).
     * @param destLatitude Latitude de destino.
     * @param destLongitude Longitude de destino.
     * @param destinationLabel Rótulo descritivo do destino para rotulagem no mapa.
     * @return [NavigationIntentSpec] pronta para conversão em `android.content.Intent`.
     */
    fun buildNavigationSpec(
        app: ExternalNavigationApp,
        destLatitude: Double,
        destLongitude: Double,
        destinationLabel: String? = null
    ): NavigationIntentSpec {
        return when (app) {
            ExternalNavigationApp.GOOGLE_MAPS -> {
                // Formato oficial do Google Maps Navigation: google.navigation:q=latitude,longitude
                val labelParam = destinationLabel?.let { "($it)" } ?: ""
                val uri = "google.navigation:q=$destLatitude,$destLongitude$labelParam"
                NavigationIntentSpec(
                    action = ACTION_VIEW,
                    uriString = uri,
                    targetPackage = PACKAGE_GOOGLE_MAPS
                )
            }
            ExternalNavigationApp.WAZE -> {
                // Formato oficial de deep link do Waze: https://waze.com/ul?ll=lat,lng&navigate=yes
                val uri = "https://waze.com/ul?ll=$destLatitude,$destLongitude&navigate=yes"
                NavigationIntentSpec(
                    action = ACTION_VIEW,
                    uriString = uri,
                    targetPackage = PACKAGE_WAZE
                )
            }
            ExternalNavigationApp.SYSTEM_DEFAULT -> {
                // Formato RFC 5870 padrão do Android: geo:lat,lng?q=lat,lng(label)
                val labelParam = destinationLabel?.let { "($it)" } ?: ""
                val uri = "geo:$destLatitude,$destLongitude?q=$destLatitude,$destLongitude$labelParam"
                NavigationIntentSpec(
                    action = ACTION_VIEW,
                    uriString = uri,
                    targetPackage = null
                )
            }
        }
    }
}
