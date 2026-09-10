package br.com.wgc.offline_sync.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Estado da interface do motor de sincronizaÃ§Ã£o offline e fila de transaÃ§Ãµes pendentes.
 *
 * @property title TÃ­tulo do serviÃ§o de sincronizaÃ§Ã£o.
 * @property isLoading Indica se a fila de sincronizaÃ§Ã£o estÃ¡ ativa e processando dados.
 */
data class OfflineSyncUiState(
    val title: String = "MÃ³dulo de SincronizaÃ§Ã£o Offline WGC",
    val isLoading: Boolean = false
)

/**
 * ViewModel encarregado da reconciliaÃ§Ã£o de dados locais (Room) com o servidor remoto (OmniBackend).
 */
@HiltViewModel
class OfflineSyncViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OfflineSyncUiState())

    /** Fluxo observÃ¡vel com o status atual das operaÃ§Ãµes da fila offline. */
    val uiState: StateFlow<OfflineSyncUiState> = _uiState
}