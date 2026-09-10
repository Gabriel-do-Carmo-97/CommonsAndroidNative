package br.com.wgc.maps.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutÃ¡vel da visualizaÃ§Ã£o do mapa e marcadores geogrÃ¡ficos.
 *
 * @property title TÃ­tulo do mÃ³dulo de navegaÃ§Ã£o.
 * @property isLoading Indicador de carregamento de tiles ou rotas.
 */
data class MapUiState(
    val title: String = "MÃ³dulo de Mapas e GeolocalizaÃ§Ã£o",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pela coordenaÃ§Ã£o de camadas do mapa e renderizaÃ§Ã£o de rotas.
 */
@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())

    /**
     * Fluxo observÃ¡vel com o estado da interface do mapa.
     */
    val uiState: StateFlow<MapUiState> = _uiState
}