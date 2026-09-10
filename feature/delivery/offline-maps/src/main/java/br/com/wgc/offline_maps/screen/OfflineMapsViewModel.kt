package br.com.wgc.offline_maps.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class OfflineMapsUiState(val title: String = "Módulo de Mapas Offline WGC", val isLoading: Boolean = false)

@HiltViewModel
class OfflineMapsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OfflineMapsUiState())
    val uiState: StateFlow<OfflineMapsUiState> = _uiState
}
