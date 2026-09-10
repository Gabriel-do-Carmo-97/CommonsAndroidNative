package br.com.wgc.geofencing.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutÃ¡vel do gerenciador de cercamento virtual.
 *
 * @property title TÃ­tulo do painel de geofencing.
 * @property isLoading Sinalizador de cadastro ou atualizaÃ§Ã£o assÃ­ncrona de cercas.
 */
data class GeofencingUiState(
    val title: String = "MÃ³dulo de Cercas Virtuais WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pela criaÃ§Ã£o, deleÃ§Ã£o e escuta de eventos de transiÃ§Ã£o de geofences.
 */
@HiltViewModel
class GeofencingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(GeofencingUiState())

    /**
     * Fluxo reativo do estado do mÃ³dulo de cercas virtuais.
     */
    val uiState: StateFlow<GeofencingUiState> = _uiState
}