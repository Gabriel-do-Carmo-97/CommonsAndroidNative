package br.com.gds.order_tracking.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class OrderTrackingUiState(val title: String = "Módulo de Acompanhamento de Pedido", val isLoading: Boolean = false)

@HiltViewModel
class OrderTrackingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(OrderTrackingUiState())
    val uiState: StateFlow<OrderTrackingUiState> = _uiState
}
