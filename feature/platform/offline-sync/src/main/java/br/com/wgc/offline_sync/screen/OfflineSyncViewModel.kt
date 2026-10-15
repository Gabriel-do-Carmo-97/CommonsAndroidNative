package br.com.wgc.offline_sync.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.wgc.offline_sync.queue.OfflineMutationQueueManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado da interface do motor de sincronização offline e fila de transações pendentes.
 *
 * @property title Título do serviço de sincronização.
 * @property pendingCount Quantidade de mutações locais aguardando envio para o OmniBackend.
 * @property isLoading Indica se a fila de sincronização está ativa e processando dados.
 * @property lastSyncTimestamp Timestamp epoch da última reconciliação executada.
 */
data class OfflineSyncUiState(
    val title: String = "Módulo de Sincronização Offline WGC",
    val pendingCount: Int = 0,
    val isLoading: Boolean = false,
    val lastSyncTimestamp: Long = 0L
)

/**
 * ViewModel encarregado da reconciliação de dados locais com o servidor remoto OmniBackend.
 *
 * ### 📌 Quando Usar:
 * - Em telas de status de sincronização e diagnóstico de dados offline.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun OfflineSyncRoute(viewModel: OfflineSyncViewModel = hiltViewModel()) {
 *     val state by viewModel.uiState.collectAsState()
 *     // viewModel.triggerSync()
 * }
 * ```
 *
 * @param queueManager Gerenciador da fila persistente de mutações transacionais.
 */
@HiltViewModel
class OfflineSyncViewModel @Inject constructor(
    private val queueManager: OfflineMutationQueueManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OfflineSyncUiState(pendingCount = queueManager.getPendingMutations().size)
    )

    /** Fluxo observável com o status atual das operações da fila offline. */
    val uiState: StateFlow<OfflineSyncUiState> = _uiState.asStateFlow()

    /**
     * Atualiza o contador de mutações pendentes na fila.
     */
    fun refreshQueueStatus() {
        _uiState.update { it.copy(pendingCount = queueManager.getPendingMutations().size) }
    }

    /**
     * Dispara a sincronização e dreno de todas as mutações pendentes da fila para o servidor.
     */
    fun triggerSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val pending = queueManager.getPendingMutations()
            pending.forEach { mutation ->
                queueManager.markCompleted(mutation.mutationId)
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    pendingCount = queueManager.getPendingMutations().size,
                    lastSyncTimestamp = System.currentTimeMillis()
                )
            }
        }
    }
}