package br.com.wgc.emergency.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado da interface do mÃ³dulo de socorro e envio de alertas de pÃ¢nico.
 *
 * @property title TÃ­tulo identificador do serviÃ§o de emergÃªncia.
 * @property isLoading Indica se o acionamento dos canais de resgate estÃ¡ em andamento.
 */
data class EmergencyUiState(
    val title: String = "MÃ³dulo de BotÃ£o de PÃ¢nico WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pela emissÃ£o de alertas de emergÃªncia e transmissÃ£o de coordenadas em tempo real.
 */
@HiltViewModel
class EmergencyViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EmergencyUiState())

    /** Fluxo de estado com os dados de transmissÃ£o de alerta. */
    val uiState: StateFlow<EmergencyUiState> = _uiState
}