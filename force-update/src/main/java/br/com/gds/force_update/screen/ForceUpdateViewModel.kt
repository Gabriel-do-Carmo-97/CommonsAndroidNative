package br.com.gds.force_update.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class ForceUpdateUiState(val title: String = "Módulo de Force Update e Manutenção", val isLoading: Boolean = false)

@HiltViewModel
class ForceUpdateViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ForceUpdateUiState())
    val uiState: StateFlow<ForceUpdateUiState> = _uiState
}
