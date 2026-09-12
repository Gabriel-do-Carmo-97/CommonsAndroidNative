package br.com.wgc.message.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutável da listagem geral de conversas de atendimento.
 *
 * @property title Título do cabeçalho da central de mensagens.
 * @property isLoading Indicador de carregamento de conversas em andamento.
 */
data class MessageUiState(
    val title: String = "Módulo de Mensageria e Chat",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pelo painel geral e listagem de conversas ativas.
 */
@HiltViewModel
class MessageViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MessageUiState())

    /**
     * Fluxo observável do estado do painel de mensagens.
     */
    val uiState: StateFlow<MessageUiState> = _uiState
}