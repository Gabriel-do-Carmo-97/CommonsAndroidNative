package br.com.wgc.driver_app.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado imutável da interface do aplicativo do motorista/entregador.
 *
 * @property title Título identificador do módulo operacional.
 * @property isLoading Sinalizador de sincronização em segundo plano.
 */
data class DriverAppUiState(
    val title: String = "Módulo do Entregador e Logística WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel responsável pelo ciclo operacional e recebimento de corridas pelo entregador parceiro.
 */
@HiltViewModel
class DriverAppViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DriverAppUiState())

    /**
     * Fluxo observável contendo o estado operacional do motorista.
     */
    val uiState: StateFlow<DriverAppUiState> = _uiState
}