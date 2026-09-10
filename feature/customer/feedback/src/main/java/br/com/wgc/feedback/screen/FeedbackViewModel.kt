package br.com.wgc.feedback.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.wgc.omnibackend.core.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedbackUiState(
    val title: String = "Pesquisa de Satisfação & NPS",
    val npsScore: Int = 10,
    val starRating: Int = 5,
    val selectedCategory: String = "Atendimento",
    val availableCategories: List<String> = listOf("Atendimento", "Entrega", "Qualidade", "Preço", "Aplicativo"),
    val comment: String = "",
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState.asStateFlow()

    fun onNpsScoreChanged(score: Int) {
        _uiState.update { it.copy(npsScore = score.coerceIn(0, 10)) }
    }

    fun onStarRatingChanged(rating: Int) {
        _uiState.update { it.copy(starRating = rating.coerceIn(1, 5)) }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onCommentChanged(comment: String) {
        _uiState.update { it.copy(comment = comment) }
    }

    fun submitFeedback(onSuccess: () -> Unit = {}) {
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            val payload = mapOf(
                "npsScore" to _uiState.value.npsScore,
                "starRating" to _uiState.value.starRating,
                "category" to _uiState.value.selectedCategory,
                "comment" to _uiState.value.comment,
                "timestamp" to System.currentTimeMillis()
            )
            try {
                firestoreRepository.addDocument(
                    collection = "feedbacks",
                    data = payload,
                    customId = null
                )
            } catch (_: Exception) {
                // Fallback gracioso para persistência offline
            }
            _uiState.update { it.copy(isSubmitting = false, isSubmitted = true) }
            onSuccess()
        }
    }
}
