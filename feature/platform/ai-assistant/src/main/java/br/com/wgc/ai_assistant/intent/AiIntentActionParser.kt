package br.com.wgc.ai_assistant.intent

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tipos de intenções mapeadas para execução de atalhos e navegações no aplicativo.
 */
enum class AiIntentType {
    /** Recompra em 1 clique ou repetição do último pedido transacionado. */
    REORDER_LAST_PURCHASE,
    /** Consulta de status em tempo real da entrega ou remessa ativa. */
    TRACK_ACTIVE_ORDER,
    /** Pesquisa direta por termos ou produtos no catálogo da loja. */
    SEARCH_CATALOG,
    /** Abertura da central de ajuda ou transbordo para atendimento humano. */
    OPEN_SUPPORT,
    /** Consulta geral que não dispara atalho de tela (continua conversa natural). */
    CONVERSATIONAL_ONLY
}

/**
 * Ação compilada a partir da interpretação de linguagem natural (texto ou voz transcrita).
 *
 * @property intentType Intenção identificada ([AiIntentType]).
 * @property confidence Nível de confiança na classificação (0.0 a 1.0).
 * @property parameters Parâmetros extraídos da frase (ex: termo de busca, número de pedido).
 * @property targetNavigationRoute Rota interna de navegação associada à ação.
 * @property userFriendlyResponse Resposta verbal imediata do assistente antes do redirecionamento.
 */
data class AiParsedAction(
    val intentType: AiIntentType,
    val confidence: Double,
    val parameters: Map<String, String> = emptyMap(),
    val targetNavigationRoute: String,
    val userFriendlyResponse: String
)

/**
 * Interpretador corporativo de Intenções e Ações Rápidas por Comando de Voz/Texto.
 *
 * Transforma comandos falados ou digitados pelo usuário em ações executáveis nativas do app
 * (como recompra de itens, rastreamento de entregas ou busca no catálogo) sem necessidade de navegação manual.
 *
 * ### 📌 Quando Usar:
 * Acione no [br.com.wgc.ai_assistant.screen.AiAssistantViewModel] assim que o usuário submeter
 * uma mensagem para avaliar se ela deve acionar um atalho de deep linking instantâneo.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val parser = AiIntentActionParser()
 * val action = parser.parseUserInput("Onde está a minha entrega de pizza?")
 * if (action.intentType == AiIntentType.TRACK_ACTIVE_ORDER) {
 *     navController.navigate(action.targetNavigationRoute)
 * }
 * ```
 */
@Singleton
class AiIntentActionParser @Inject constructor() {

    /**
     * Interpreta a entrada em linguagem natural e infere a ação correspondente.
     *
     * @param rawText Frase digitada ou transcrita do microfone.
     * @return [AiParsedAction] contendo intenção, parâmetros e rota de destino.
     */
    fun parseUserInput(rawText: String): AiParsedAction {
        val normalized = rawText.lowercase().trim()

        return when {
            containsAny(
                normalized,
                "repetir",
                "recompra",
                "pedir novamente",
                "pedir de novo",
                "o mesmo pedido"
            ) -> {
                AiParsedAction(
                    intentType = AiIntentType.REORDER_LAST_PURCHASE,
                    confidence = 0.95,
                    targetNavigationRoute = "wgc://checkout/cart/reorder",
                    userFriendlyResponse = "Preparando a recompra do seu último pedido agora mesmo!"
                )
            }
            containsAny(
                normalized,
                "onde está",
                "rastrear",
                "rastreio",
                "status da entrega",
                "cadê meu pedido",
                "a caminho"
            ) -> {
                AiParsedAction(
                    intentType = AiIntentType.TRACK_ACTIVE_ORDER,
                    confidence = 0.92,
                    targetNavigationRoute = "wgc://delivery/tracking/active",
                    userFriendlyResponse = "Abrindo o mapa de rastreamento ao vivo para você."
                )
            }
            containsAny(
                normalized,
                "buscar",
                "procurar",
                "pesquisar",
                "encontrar",
                "quero comprar",
                "tem"
            ) -> {
                val query = extractSearchQuery(normalized)
                AiParsedAction(
                    intentType = AiIntentType.SEARCH_CATALOG,
                    confidence = 0.88,
                    parameters = mapOf("query" to query),
                    targetNavigationRoute = "wgc://catalog/search?query=$query",
                    userFriendlyResponse = "Buscando \"$query\" no catálogo para você."
                )
            }
            containsAny(
                normalized,
                "suporte",
                "ajuda",
                "atendente",
                "falar com humano",
                "problema",
                "reclamação"
            ) -> {
                AiParsedAction(
                    intentType = AiIntentType.OPEN_SUPPORT,
                    confidence = 0.90,
                    targetNavigationRoute = "wgc://support/chat",
                    userFriendlyResponse = "Encaminhando seu atendimento para nossa central de suporte."
                )
            }
            else -> {
                AiParsedAction(
                    intentType = AiIntentType.CONVERSATIONAL_ONLY,
                    confidence = 0.70,
                    targetNavigationRoute = "",
                    userFriendlyResponse = "Estou consultando seus dados para responder da melhor forma."
                )
            }
        }
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { keyword ->
            val pattern = "(?i)(^|\\s|\\W)" + Regex.escape(keyword) + "($|\\s|\\W)"
            pattern.toRegex().containsMatchIn(text)
        }
    }

    private fun extractSearchQuery(text: String): String {
        var query = text
        listOf("buscar", "procurar", "pesquisar", "encontrar", "quero comprar", "tem").forEach { keyword ->
            val regex = "(?i)(^|\\s|\\W)" + Regex.escape(keyword) + "($|\\s|\\W)"
            query = query.replace(regex.toRegex(), " ")
        }
        return query.trim(' ', '?', '!', '.', ',')
    }
}
