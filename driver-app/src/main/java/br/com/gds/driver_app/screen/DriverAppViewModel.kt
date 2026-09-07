package br.com.gds.driver_app.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class DriverAppUiState(val title: String = "Módulo do Entregador e Logística WGC", val isLoading: Boolean = false)

@HiltViewModel
class DriverAppViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DriverAppUiState())
    val uiState: StateFlow<DriverAppUiState> = _uiState
}
