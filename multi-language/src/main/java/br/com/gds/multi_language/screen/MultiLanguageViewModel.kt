package br.com.gds.multi_language.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class MultiLanguageUiState(val title: String = "Módulo de Internacionalização WGC", val isLoading: Boolean = false)

@HiltViewModel
class MultiLanguageViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MultiLanguageUiState())
    val uiState: StateFlow<MultiLanguageUiState> = _uiState
}
