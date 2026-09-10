package br.com.wgc.telemetry.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado da interface do sistema de telemetria contÃ­nua.
 *
 * @property title TÃ­tulo do sistema de telemetria.
 * @property isLoading Indica se a coleta de dados de sensores estÃ¡ em execuÃ§Ã£o.
 */
data class TelemetryUiState(
    val title: String = "MÃ³dulo de Telemetria e GPS WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel que gerencia a coleta de telemetria, consumo de bateria, geolocalizaÃ§Ã£o e envio de mÃ©tricas.
 */
@HiltViewModel
class TelemetryViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TelemetryUiState())

    /** Fluxo de estado contendo leituras ativas de telemetria. */
    val uiState: StateFlow<TelemetryUiState> = _uiState
}