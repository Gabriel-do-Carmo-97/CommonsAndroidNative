package br.com.wgc.emergency.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado da interface do módulo de socorro e envio de alertas de pânico.
 *
 * @property title Título identificador do serviço de emergência.
 * @property isLoading Indica se o acionamento dos canais de resgate está em andamento.
 */
data class EmergencyUiState(
    val title: String = "Módulo de Botão de Pânico WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pela emissão de alertas de emergência e transmissão de coordenadas em tempo real.
 */
@HiltViewModel
class EmergencyViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EmergencyUiState())

    /** Fluxo de estado com os dados de transmissão de alerta. */
    val uiState: StateFlow<EmergencyUiState> = _uiState
}