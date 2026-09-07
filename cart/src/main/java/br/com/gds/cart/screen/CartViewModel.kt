package br.com.gds.cart.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class CartUiState(val title: String = "Módulo de Carrinho e Taxas de Entrega", val isLoading: Boolean = false)

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState
}
