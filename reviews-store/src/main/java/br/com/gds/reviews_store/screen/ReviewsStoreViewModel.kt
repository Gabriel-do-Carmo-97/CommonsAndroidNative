package br.com.gds.reviews_store.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class ReviewsStoreUiState(val title: String = "Módulo de Avaliação da Loja e Pedidos", val isLoading: Boolean = false)

@HiltViewModel
class ReviewsStoreViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewsStoreUiState())
    val uiState: StateFlow<ReviewsStoreUiState> = _uiState
}
