package br.com.wgc.offline_maps.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutÃ¡vel do gerenciador de pacotes de mapas offline.
 *
 * @property title TÃ­tulo do gerenciador de pacotes offline.
 * @property isLoading Indicador de download ou indexaÃ§Ã£o de mapas locais.
 */
data class OfflineMapsUiState(
    val title: String = "MÃ³dulo de Mapas Offline WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pelo download e persistÃªncia em cache de mapas geogrÃ¡ficos locais.
 */
@HiltViewModel
class OfflineMapsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OfflineMapsUiState())

    /**
     * Fluxo reativo do estado do armazenamento de mapas offline.
     */
    val uiState: StateFlow<OfflineMapsUiState> = _uiState
}