package br.com.wgc.offline_sync.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado da interface do motor de sincronização offline e fila de transações pendentes.
 *
 * @property title Título do serviço de sincronização.
 * @property isLoading Indica se a fila de sincronização está ativa e processando dados.
 */
data class OfflineSyncUiState(
    val title: String = "Módulo de Sincronização Offline WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel encarregado da reconciliação de dados locais (Room) com o servidor remoto (OmniBackend).
 */
@HiltViewModel
class OfflineSyncViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OfflineSyncUiState())

    /** Fluxo observável com o status atual das operações da fila offline. */
    val uiState: StateFlow<OfflineSyncUiState> = _uiState
}