package br.com.wgc.dispatch.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutÃ¡vel do painel operacional de despacho e distribuiÃ§Ã£o.
 *
 * @property title TÃ­tulo do cabeÃ§alho do mÃ³dulo.
 * @property isLoading Sinalizador de requisiÃ§Ãµes de alocaÃ§Ã£o em processamento.
 */
data class DispatchUiState(
    val title: String = "MÃ³dulo de Dispatch e AlocaÃ§Ã£o WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pela coordenaÃ§Ã£o e monitoramento das ordens de despacho logÃ­stico.
 */
@HiltViewModel
class DispatchViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DispatchUiState())

    /**
     * Fluxo observÃ¡vel do estado do despacho logÃ­stico.
     */
    val uiState: StateFlow<DispatchUiState> = _uiState
}