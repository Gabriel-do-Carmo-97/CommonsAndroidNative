package br.com.gds.whatsapp_direct.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class WhatsappDirectUiState(val title: String = "Módulo de Envio Direto para WhatsApp", val isLoading: Boolean = false)

@HiltViewModel
class WhatsappDirectViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(WhatsappDirectUiState())
    val uiState: StateFlow<WhatsappDirectUiState> = _uiState
}
