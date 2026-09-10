package br.com.gds.telemetry.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class TelemetryUiState(val title: String = "Módulo de Telemetria e GPS WGC", val isLoading: Boolean = false)

@HiltViewModel
class TelemetryViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TelemetryUiState())
    val uiState: StateFlow<TelemetryUiState> = _uiState
}
