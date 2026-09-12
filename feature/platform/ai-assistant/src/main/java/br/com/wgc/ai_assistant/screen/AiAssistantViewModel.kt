package br.com.wgc.ai_assistant.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado da interface do assistente de inteligência artificial generativa.
 *
 * @property title Título do assistente virtual.
 * @property isLoading Indica se a geração de resposta via IA está em progresso.
 */
data class AiAssistantUiState(
    val title: String = "Módulo de Assistente de IA WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pelo processamento de prompts, contexto de conversa e integração LLM.
 */
@HiltViewModel
class AiAssistantViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AiAssistantUiState())

    /** Fluxo de estado contendo o histórico de mensagens e status do assistente. */
    val uiState: StateFlow<AiAssistantUiState> = _uiState
}