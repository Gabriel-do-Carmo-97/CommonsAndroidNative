package br.com.gds.promotions.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class PromotionsUiState(val title: String = "Módulo de Promocões e Cupons", val isLoading: Boolean = false)

@HiltViewModel
class PromotionsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PromotionsUiState())
    val uiState: StateFlow<PromotionsUiState> = _uiState
}
