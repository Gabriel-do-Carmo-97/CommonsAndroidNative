package br.com.wgc.offline_maps.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutável do gerenciador de pacotes de mapas offline.
 *
 * @property title Título do gerenciador de pacotes offline.
 * @property isLoading Indicador de download ou indexação de mapas locais.
 */
data class OfflineMapsUiState(
    val title: String = "Módulo de Mapas Offline WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pelo download e persistência em cache de mapas geográficos locais.
 */
@HiltViewModel
class OfflineMapsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OfflineMapsUiState())

    /**
     * Fluxo reativo do estado do armazenamento de mapas offline.
     */
    val uiState: StateFlow<OfflineMapsUiState> = _uiState
}