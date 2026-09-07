package br.com.gds.stores.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class StoresUiState(val title: String = "Módulo de Multi-Lojas e Filiais", val isLoading: Boolean = false)

@HiltViewModel
class StoresViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(StoresUiState())
    val uiState: StateFlow<StoresUiState> = _uiState
}
