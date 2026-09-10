package br.com.gds.subscriptions.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class SubscriptionsUiState(val title: String = "Módulo de Assinaturas e Recorrência WGC", val isLoading: Boolean = false)

@HiltViewModel
class SubscriptionsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SubscriptionsUiState())
    val uiState: StateFlow<SubscriptionsUiState> = _uiState
}
