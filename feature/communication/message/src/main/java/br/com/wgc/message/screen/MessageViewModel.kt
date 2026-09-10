package br.com.wgc.message.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutÃ¡vel da listagem geral de conversas de atendimento.
 *
 * @property title TÃ­tulo do cabeÃ§alho da central de mensagens.
 * @property isLoading Indicador de carregamento de conversas em andamento.
 */
data class MessageUiState(
    val title: String = "MÃ³dulo de Mensageria e Chat",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pelo painel geral e listagem de conversas ativas.
 */
@HiltViewModel
class MessageViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MessageUiState())

    /**
     * Fluxo observÃ¡vel do estado do painel de mensagens.
     */
    val uiState: StateFlow<MessageUiState> = _uiState
}