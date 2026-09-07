package br.com.gds.search.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class SearchUiState(val title: String = "Módulo de Busca Reativa e Filtros", val isLoading: Boolean = false)

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState
}
