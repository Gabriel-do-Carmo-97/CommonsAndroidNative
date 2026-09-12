package br.com.wgc.telemetry.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado da interface do sistema de telemetria contínua.
 *
 * @property title Título do sistema de telemetria.
 * @property isLoading Indica se a coleta de dados de sensores está em execução.
 */
data class TelemetryUiState(
    val title: String = "Módulo de Telemetria e GPS WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel que gerencia a coleta de telemetria, consumo de bateria, geolocalização e envio de métricas.
 */
@HiltViewModel
class TelemetryViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TelemetryUiState())

    /** Fluxo de estado contendo leituras ativas de telemetria. */
    val uiState: StateFlow<TelemetryUiState> = _uiState
}