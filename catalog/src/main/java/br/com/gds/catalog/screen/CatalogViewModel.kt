package br.com.gds.catalog.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class CatalogUiState(val title: String = "Módulo de Catálogo e Cardápio Digital", val isLoading: Boolean = false)

@HiltViewModel
class CatalogViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState
}
