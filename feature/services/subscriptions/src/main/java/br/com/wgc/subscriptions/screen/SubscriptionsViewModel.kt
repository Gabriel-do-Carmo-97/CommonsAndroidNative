package br.com.wgc.subscriptions.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutável do painel de assinaturas e recorrência.
 *
 * @property title Título identificador do módulo.
 * @property isLoading Sinalizador de requisições de faturamento em processamento.
 */
data class SubscriptionsUiState(
    val title: String = "Módulo de Assinaturas e Recorrência WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pelo controle de planos e cobranças periódicas do usuário.
 */
@HiltViewModel
class SubscriptionsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SubscriptionsUiState())

    /**
     * Fluxo reativo do estado das assinaturas.
     */
    val uiState: StateFlow<SubscriptionsUiState> = _uiState
}