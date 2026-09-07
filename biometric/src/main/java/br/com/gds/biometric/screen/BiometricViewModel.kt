package br.com.gds.biometric.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class BiometricUiState(val title: String = "Módulo de Biometria, PIN e App Lock", val isLoading: Boolean = false)

@HiltViewModel
class BiometricViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(BiometricUiState())
    val uiState: StateFlow<BiometricUiState> = _uiState
}
