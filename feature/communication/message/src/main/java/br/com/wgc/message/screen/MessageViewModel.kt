package br.com.wgc.message.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class MessageUiState(
    val title: String = "Módulo de Mensageria e Chat",
    val isLoading: Boolean = false
)

@HiltViewModel
class MessageViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MessageUiState())
    val uiState: StateFlow<MessageUiState> = _uiState
}
