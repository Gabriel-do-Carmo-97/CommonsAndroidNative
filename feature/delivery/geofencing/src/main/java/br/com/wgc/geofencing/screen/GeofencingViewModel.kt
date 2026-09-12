package br.com.wgc.geofencing.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutável do gerenciador de cercamento virtual.
 *
 * @property title Título do painel de geofencing.
 * @property isLoading Sinalizador de cadastro ou atualização assíncrona de cercas.
 */
data class GeofencingUiState(
    val title: String = "Módulo de Cercas Virtuais WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pela criação, deleção e escuta de eventos de transição de geofences.
 */
@HiltViewModel
class GeofencingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(GeofencingUiState())

    /**
     * Fluxo reativo do estado do módulo de cercas virtuais.
     */
    val uiState: StateFlow<GeofencingUiState> = _uiState
}