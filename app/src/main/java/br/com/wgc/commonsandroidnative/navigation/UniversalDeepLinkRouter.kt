package br.com.wgc.commonsandroidnative.navigation

import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Destino de navegação parseado a partir de um Deep Link corporativo.
 *
 * @property route Rota interna para consumo pelo NavHost ou gerenciador de telas.
 * @property parameters Mapa com query params ou argumentos de caminho extraídos da URI.
 */
data class DeepLinkResolution(
    val route: String,
    val parameters: Map<String, String> = emptyMap()
)

/**
 * Roteador corporativo de Deep Links Universais (HTTP/HTTPS App Links) e esquemas customizados (`wgc://`).
 *
 * Realiza o parseamento estrito de links de entrada (`app.wgc.com.br`), validando domínios
 * permitidos e mapeando caminhos para rotas navegáveis no Compose.
 *
 * ### 📌 Quando Usar:
 * - No tratamento do Intent de entrada em `MainActivity.onCreate` ou `onNewIntent`.
 * - Ao clicar em notificações push ou links compartilhados por WhatsApp e E-mail.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val router = UniversalDeepLinkRouter()
 * val uri = intent.data // ex: https://app.wgc.com.br/product/prod_123?promo=VERAO
 * val resolution = router.resolve(uri)
 *
 * if (resolution != null) {
 *     navController.navigate(resolution.route)
 * }
 * ```
 */
@Singleton
class UniversalDeepLinkRouter @Inject constructor() {

    companion object {
        private val ALLOWED_HOSTS = setOf("app.wgc.com.br", "wgc.com.br", "app")
        private val ALLOWED_SCHEMES = setOf("https", "http", "wgc")
    }

    /**
     * Resolve uma [Uri] externa para o destino de tela interno correspondente.
     *
     * @param uri URI recebida via Intent de visualização.
     * @return [DeepLinkResolution] se o link for válido e mapeado, ou `null` caso contrário.
     */
    fun resolve(uri: Uri?): DeepLinkResolution? {
        if (uri == null) return null

        val scheme = uri.scheme?.lowercase() ?: return null
        val host = uri.host?.lowercase() ?: return null

        if (scheme !in ALLOWED_SCHEMES || host !in ALLOWED_HOSTS) {
            return null
        }

        val pathSegments = uri.pathSegments ?: emptyList()
        val queryParams = mutableMapOf<String, String>()
        uri.queryParameterNames?.forEach { key ->
            uri.getQueryParameter(key)?.let { value ->
                queryParams[key] = value
            }
        }

        if (pathSegments.isEmpty()) {
            return DeepLinkResolution(route = "home", parameters = queryParams)
        }

        return when (val firstSegment = pathSegments[0].lowercase()) {
            "catalog", "produtos" -> {
                DeepLinkResolution(route = "catalog", parameters = queryParams)
            }
            "product", "produto" -> {
                val productId = pathSegments.getOrNull(1) ?: ""
                val params = queryParams.toMutableMap()
                if (productId.isNotBlank()) params["id"] = productId
                DeepLinkResolution(route = "product_details/$productId", parameters = params)
            }
            "cart", "carrinho" -> {
                DeepLinkResolution(route = "cart", parameters = queryParams)
            }
            "checkout", "pagamento" -> {
                DeepLinkResolution(route = "checkout", parameters = queryParams)
            }
            "order", "pedido", "tracking" -> {
                val orderId = pathSegments.getOrNull(1) ?: ""
                val params = queryParams.toMutableMap()
                if (orderId.isNotBlank()) params["orderId"] = orderId
                DeepLinkResolution(route = "order_tracking/$orderId", parameters = params)
            }
            "scheduling", "agendamento" -> {
                DeepLinkResolution(route = "scheduling", parameters = queryParams)
            }
            "chat", "mensagem" -> {
                val threadId = pathSegments.getOrNull(1) ?: ""
                DeepLinkResolution(route = "chat/$threadId", parameters = queryParams)
            }
            "ai-assistant", "ia" -> {
                DeepLinkResolution(route = "ai_assistant", parameters = queryParams)
            }
            "profile", "perfil" -> {
                DeepLinkResolution(route = "profile", parameters = queryParams)
            }
            "settings", "configuracoes" -> {
                DeepLinkResolution(route = "settings", parameters = queryParams)
            }
            else -> {
                DeepLinkResolution(route = firstSegment, parameters = queryParams)
            }
        }
    }
}
