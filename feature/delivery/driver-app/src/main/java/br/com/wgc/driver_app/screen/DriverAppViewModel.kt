package br.com.wgc.driver_app.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutÃ¡vel da interface do aplicativo do motorista/entregador.
 *
 * @property title TÃ­tulo identificador do mÃ³dulo operacional.
 * @property isLoading Sinalizador de sincronizaÃ§Ã£o em segundo plano.
 */
data class DriverAppUiState(
    val title: String = "MÃ³dulo do Entregador e LogÃ­stica WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pelo ciclo operacional e recebimento de corridas pelo entregador parceiro.
 */
@HiltViewModel
class DriverAppViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DriverAppUiState())

    /**
     * Fluxo observÃ¡vel contendo o estado operacional do motorista.
     */
    val uiState: StateFlow<DriverAppUiState> = _uiState
}