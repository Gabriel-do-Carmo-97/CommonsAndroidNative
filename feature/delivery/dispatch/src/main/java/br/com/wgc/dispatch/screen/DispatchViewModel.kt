package br.com.wgc.dispatch.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class DispatchUiState(val title: String = "Módulo de Dispatch e Alocação WGC", val isLoading: Boolean = false)

@HiltViewModel
class DispatchViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DispatchUiState())
    val uiState: StateFlow<DispatchUiState> = _uiState
}
