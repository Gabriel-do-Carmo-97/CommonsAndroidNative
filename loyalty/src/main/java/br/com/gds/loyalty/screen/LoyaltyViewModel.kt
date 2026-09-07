package br.com.gds.loyalty.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class LoyaltyUiState(val title: String = "Módulo de Cartão Fidelidade e Cashback", val isLoading: Boolean = false)

@HiltViewModel
class LoyaltyViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(LoyaltyUiState())
    val uiState: StateFlow<LoyaltyUiState> = _uiState
}
