package br.com.wgc.maps.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Estado imutável da visualização do mapa e marcadores geográficos.
 *
 * ### 📌 Quando Usar:
 * Consumido pela [MapScreen] para controlar camadas vetoriais, zoom e indicadores de carregamento.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * val state = MapUiState(isLoading = false)
 * ```
 *
 * @property title Título do módulo de navegação.
 * @property isLoading Indicador de carregamento de tiles ou rotas.
 */
data class MapUiState(
    val title: String = "Módulo de Mapas e Geolocalização",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pela coordenação de camadas do mapa e renderização de rotas.
 *
 * ### 📌 Quando Usar:
 * Utilize para renderizar mapas estáticos ou interativos de navegação urbana.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDeMapa(viewModel: MapViewModel = hiltViewModel()) {
 *     val state by viewModel.uiState.collectAsState()
 * }
 * ```
 */
@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())

    /**
     * Fluxo observável com o estado da interface do mapa.
     */
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
}