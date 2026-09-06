package br.com.gds.payment.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class PaymentUiState(
    val title: String = "Módulo de Pagamentos e Checkout",
    val isLoading: Boolean = false
)

@HiltViewModel
class PaymentViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState
}
