package br.com.wgc.ai_assistant.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado da interface do assistente de inteligÃªncia artificial generativa.
 *
 * @property title TÃ­tulo do assistente virtual.
 * @property isLoading Indica se a geraÃ§Ã£o de resposta via IA estÃ¡ em progresso.
 */
data class AiAssistantUiState(
    val title: String = "MÃ³dulo de Assistente de IA WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pelo processamento de prompts, contexto de conversa e integraÃ§Ã£o LLM.
 */
@HiltViewModel
class AiAssistantViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AiAssistantUiState())

    /** Fluxo de estado contendo o histÃ³rico de mensagens e status do assistente. */
    val uiState: StateFlow<AiAssistantUiState> = _uiState
}