package br.com.wgc.emergency.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class EmergencyUiState(val title: String = "Módulo de Botão de Pânico WGC", val isLoading: Boolean = false)

@HiltViewModel
class EmergencyViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState
}
