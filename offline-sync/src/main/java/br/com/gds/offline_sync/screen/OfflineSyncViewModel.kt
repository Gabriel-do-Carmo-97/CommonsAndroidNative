package br.com.gds.offline_sync.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class OfflineSyncUiState(val title: String = "Módulo de Sincronização Offline WGC", val isLoading: Boolean = false)

@HiltViewModel
class OfflineSyncViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OfflineSyncUiState())
    val uiState: StateFlow<OfflineSyncUiState> = _uiState
}
