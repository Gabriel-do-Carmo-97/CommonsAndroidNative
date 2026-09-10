package br.com.wgc.subscriptions.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutÃ¡vel do painel de assinaturas e recorrÃªncia.
 *
 * @property title TÃ­tulo identificador do mÃ³dulo.
 * @property isLoading Sinalizador de requisiÃ§Ãµes de faturamento em processamento.
 */
data class SubscriptionsUiState(
    val title: String = "MÃ³dulo de Assinaturas e RecorrÃªncia WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pelo controle de planos e cobranÃ§as periÃ³dicas do usuÃ¡rio.
 */
@HiltViewModel
class SubscriptionsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SubscriptionsUiState())

    /**
     * Fluxo reativo do estado das assinaturas.
     */
    val uiState: StateFlow<SubscriptionsUiState> = _uiState
}