package br.com.wgc.geofencing.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class GeofencingUiState(val title: String = "Módulo de Cercas Virtuais WGC", val isLoading: Boolean = false)

@HiltViewModel
class GeofencingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(GeofencingUiState())
    val uiState: StateFlow<GeofencingUiState> = _uiState
}
