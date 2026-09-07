package br.com.gds.quotation.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class QuotationUiState(val title: String = "Módulo de Orçamentos e Serviços", val isLoading: Boolean = false)

@HiltViewModel
class QuotationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(QuotationUiState())
    val uiState: StateFlow<QuotationUiState> = _uiState
}
