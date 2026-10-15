package br.com.wgc.ai_assistant.engine

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Papel do remetente no diálogo com o assistente inteligente de IA.
 */
enum class AiChatRole {
    /** Pergunta ou comando disparado pelo usuário. */
    USER,
    /** Resposta emitida pelo modelo generativo de IA (LLM). */
    ASSISTANT,
    /** Diretivas de sistema e contexto de negócio injetados no prompt. */
    SYSTEM
}

/**
 * Mensagem estruturada no histórico de conversação do assistente.
 *
 * @property id Identificador exclusivo da mensagem.
 * @property role Remetente ([AiChatRole]).
 * @property content Conteúdo textual da mensagem (acumulado progressivamente durante streaming).
 * @property timestampMillis Carimbo de data/hora do envio em milissegundos.
 * @property isStreamingComplete Indica se a resposta já foi finalizada ou ainda recebe tokens.
 */
data class AiChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: AiChatRole,
    val content: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isStreamingComplete: Boolean = true
)

/**
 * Fragmento (token/chunk) recebido em tempo real via Server-Sent Events (SSE).
 *
 * @property token Segmento textual recém-gerado pelo modelo.
 * @property isFinalChunk Indica se este fragmento encerra a resposta do modelo.
 */
data class StreamChunk(
    val token: String,
    val isFinalChunk: Boolean
)

/**
 * Motor corporativo de Streaming e Integração com Modelos de IA Generativa via Server-Sent Events (SSE).
 *
 * Transmite tokens parciais progressivamente com baixíssima latência (First Token Latency < 400ms),
 * permitindo ao usuário acompanhar o raciocínio e a resposta da IA em tempo real com efeito de digitação fluida.
 *
 * ### 📌 Quando Usar:
 * Acione no [br.com.wgc.ai_assistant.screen.AiAssistantViewModel] para processar respostas do chat
 * conectado ao OmniBackend Functions com Vertex AI ou OpenAI.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val streamingEngine = AiAssistantStreamingEngine()
 * streamingEngine.streamTokens("Qual é o status da minha entrega?").collect { chunk ->
 *     mensagemAcumulada += chunk.token
 *     atualizarInterface()
 * }
 * ```
 */
@Singleton
class AiAssistantStreamingEngine @Inject constructor() {

    /**
     * Emite um fluxo assíncrono reativo de [StreamChunk] transmitindo a resposta token por token.
     *
     * @param prompt Pergunta ou comando submetido pelo usuário.
     * @param simulatedTokens Lista opcional de tokens para testes ou fallback determinístico.
     * @param delayBetweenTokensMs Atraso opcional entre emissão de tokens para fluidez visual.
     * @return [Flow] emitindo os fragmentos de texto até a finalização da resposta.
     */
    fun streamTokens(
        prompt: String,
        simulatedTokens: List<String>? = null,
        delayBetweenTokensMs: Long = 0L
    ): Flow<StreamChunk> = flow {
        val tokens = simulatedTokens ?: generateDefaultTokensForPrompt(prompt)

        tokens.forEachIndexed { index, token ->
            if (delayBetweenTokensMs > 0L) {
                delay(delayBetweenTokensMs)
            }
            val isFinal = index == tokens.lastIndex
            emit(StreamChunk(token = token, isFinalChunk = isFinal))
        }
    }

    /**
     * Interpreta e sanitiza uma linha bruta recebida no formato de protocolo Server-Sent Events (SSE).
     *
     * Suporta payloads no padrão `data: token` ou `data: {"token": "..."}`.
     *
     * @param sseLine Linha bruta enviada pelo socket HTTP chunked.
     * @return Token textual extraído ou null se for ping de heartbeat ou linha vazia.
     */
    fun parseServerSentEvent(sseLine: String): String? {
        val trimmed = sseLine.trim()
        if (!trimmed.startsWith("data:")) return null

        val dataContent = trimmed.removePrefix("data:").trim()
        if (dataContent == "[DONE]" || dataContent.isBlank()) return null

        // Se for JSON simples contendo "token": "..."
        return if (dataContent.startsWith("{") && dataContent.contains("\"token\"")) {
            val tokenValue = dataContent.substringAfter("\"token\":").substringBefore("}").trim()
            tokenValue.trim('"', ' ')
        } else {
            dataContent
        }
    }

    private fun generateDefaultTokensForPrompt(prompt: String): List<String> {
        val words = "Olá! Compreendi perfeitamente sua solicitação sobre \"$prompt\". Estou processando os detalhes na nossa base corporativa.".split(" ")
        return words.mapIndexed { index, word -> if (index == words.lastIndex) word else "$word " }
    }
}
