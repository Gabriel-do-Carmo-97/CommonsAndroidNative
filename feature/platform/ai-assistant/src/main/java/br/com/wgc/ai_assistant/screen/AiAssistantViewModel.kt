package br.com.wgc.ai_assistant.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.wgc.ai_assistant.engine.AiAssistantStreamingEngine
import br.com.wgc.ai_assistant.engine.AiStreamEvent
import br.com.wgc.ai_assistant.intent.AiIntentActionParser
import br.com.wgc.ai_assistant.intent.AiParsedAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Mensagem do chat com a inteligência artificial generativa.
 *
 * @property id Identificador único da mensagem.
 * @property text Conteúdo textual.
 * @property isFromUser Indica se a mensagem foi enviada pelo usuário (`true`) ou pela IA (`false`).
 * @property parsedAction Ação rápida reconhecida para execução em navegação.
 */
data class ChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val parsedAction: AiParsedAction? = null
)

/**
 * Estado da interface do assistente de inteligência artificial generativa.
 *
 * @property title Título do assistente virtual.
 * @property messages Histórico de interações no chat atual.
 * @property currentStreamingText Texto que está sendo renderizado token a token em tempo real.
 * @property isLoading Indica se a geração de resposta via IA está em progresso.
 * @property triggeredAction Ação acionada automaticamente para navegação ou atalho.
 */
data class AiAssistantUiState(
    val title: String = "Módulo de Assistente de IA WGC",
    val messages: List<ChatMessage> = emptyList(),
    val currentStreamingText: String = "",
    val isLoading: Boolean = false,
    val triggeredAction: AiParsedAction? = null
)

/**
 * ViewModel responsável pelo processamento de prompts, contexto de conversa e integração LLM.
 *
 * ### 📌 Quando Usar:
 * - Na tela do assistente virtual contextual (`:feature:platform:ai-assistant`).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun AiAssistantRoute(viewModel: AiAssistantViewModel = hiltViewModel()) {
 *     val state by viewModel.uiState.collectAsState()
 *     // viewModel.sendPrompt("repetir meu último pedido")
 * }
 * ```
 *
 * @param streamingEngine Motor de streaming token-a-token via Server-Sent Events.
 * @param intentParser Analisador semântico de intenções e atalhos do app.
 */
@HiltViewModel
class AiAssistantViewModel @Inject constructor(
    private val streamingEngine: AiAssistantStreamingEngine,
    private val intentParser: AiIntentActionParser
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiAssistantUiState())

    /** Fluxo de estado contendo o histórico de mensagens e status do assistente. */
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    /**
     * Envia um prompt do usuário, detecta intenções estruturadas e inicia streaming de resposta.
     *
     * @param prompt Texto digitado ou falado pelo usuário.
     */
    fun sendPrompt(prompt: String) {
        if (prompt.isBlank()) return

        val userMessage = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            text = prompt,
            isFromUser = true
        )

        // 1. Parsear intenções rápidas do app
        val parsedAction = intentParser.parseUserInput(prompt)

        _uiState.update { current ->
            current.copy(
                messages = current.messages + userMessage,
                isLoading = true,
                currentStreamingText = "",
                triggeredAction = parsedAction
            )
        }

        // 2. Transmissão streaming da IA
        viewModelScope.launch {
            val responseBuffer = StringBuilder()

            streamingEngine.streamCompletion(prompt).collect { event ->
                when (event) {
                    is AiStreamEvent.Token -> {
                        responseBuffer.append(event.token)
                        _uiState.update { it.copy(currentStreamingText = responseBuffer.toString()) }
                    }
                    is AiStreamEvent.Completed -> {
                        val assistantMessage = ChatMessage(
                            id = "ai_${System.currentTimeMillis()}",
                            text = event.fullText,
                            isFromUser = false,
                            parsedAction = parsedAction
                        )
                        _uiState.update { current ->
                            current.copy(
                                messages = current.messages + assistantMessage,
                                currentStreamingText = "",
                                isLoading = false
                            )
                        }
                    }
                    is AiStreamEvent.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }
}