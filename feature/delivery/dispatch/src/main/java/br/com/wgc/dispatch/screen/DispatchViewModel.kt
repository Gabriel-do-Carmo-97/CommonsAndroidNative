package br.com.wgc.dispatch.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutável do painel operacional de despacho e distribuição.
 *
 * @property title Título do cabeçalho do módulo.
 * @property isLoading Sinalizador de requisições de alocação em processamento.
 */
data class DispatchUiState(
    val title: String = "Módulo de Dispatch e Alocação WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pela coordenação e monitoramento das ordens de despacho logístico.
 */
@HiltViewModel
class DispatchViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DispatchUiState())

    /**
     * Fluxo observável do estado do despacho logístico.
     */
    val uiState: StateFlow<DispatchUiState> = _uiState
}