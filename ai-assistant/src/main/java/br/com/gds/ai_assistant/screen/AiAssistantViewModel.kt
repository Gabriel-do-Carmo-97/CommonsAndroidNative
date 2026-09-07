package br.com.gds.ai_assistant.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class AiAssistantUiState(val title: String = "Módulo de Assistente de IA WGC", val isLoading: Boolean = false)

@HiltViewModel
class AiAssistantViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState
}
