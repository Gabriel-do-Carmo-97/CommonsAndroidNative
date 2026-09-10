package br.com.wgc.maps.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class MapUiState(
    val title: String = "Módulo de Mapas e Geolocalização",
    val isLoading: Boolean = false
)

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState
}
