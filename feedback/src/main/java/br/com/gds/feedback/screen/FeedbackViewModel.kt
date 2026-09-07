package br.com.gds.feedback.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class FeedbackUiState(val title: String = "Módulo de Feedback, NPS e In-App Review", val isLoading: Boolean = false)

@HiltViewModel
class FeedbackViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState
}
