package br.com.gds.reviews_store.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class ReviewRedirectTarget {
    NONE,
    PLAY_STORE,
    INTERNAL_FEEDBACK
}

data class ReviewsStoreUiState(
    val title: String = "Avaliação na Google Play Store",
    val starRating: Int = 0,
    val hasVoted: Boolean = false,
    val feedbackComment: String = "",
    val redirectTarget: ReviewRedirectTarget = ReviewRedirectTarget.NONE,
    val isCompleted: Boolean = false
)

@HiltViewModel
class ReviewsStoreViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsStoreUiState())
    val uiState: StateFlow<ReviewsStoreUiState> = _uiState.asStateFlow()

    fun selectRating(stars: Int) {
        val target = if (stars >= 4) {
            ReviewRedirectTarget.PLAY_STORE
        } else {
            ReviewRedirectTarget.INTERNAL_FEEDBACK
        }
        _uiState.update {
            it.copy(
                starRating = stars.coerceIn(1, 5),
                hasVoted = true,
                redirectTarget = target
            )
        }
    }

    fun updateFeedbackComment(comment: String) {
        _uiState.update { it.copy(feedbackComment = comment) }
    }

    fun completeReview() {
        _uiState.update { it.copy(isCompleted = true) }
    }

    fun reset() {
        _uiState.update { ReviewsStoreUiState() }
    }
}
