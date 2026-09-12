package br.com.wgc.reviews_store.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Destino para redirecionamento após a seleção da quantidade de estrelas pelo usuário.
 */
enum class ReviewRedirectTarget {
    /** Nenhuma ação selecionada ainda. */
    NONE,
    /** Redirecionamento para a página oficial do app na Google Play Store (4 ou 5 estrelas). */
    PLAY_STORE,
    /** Direcionamento para coleta de sugestão interna de melhoria (1 a 3 estrelas). */
    INTERNAL_FEEDBACK
}

/**
 * Estado imutável do fluxo de avaliação do app na Play Store.
 *
 * @property title Título do cabeçalho da avaliação.
 * @property starRating Quantidade de estrelas selecionada (0 a 5).
 * @property hasVoted Sinalizador indicando se o usuário já selecionou uma quantidade de estrelas.
 * @property feedbackComment Comentário textual interno para notas baixas.
 * @property redirectTarget Destino da triagem da avaliação.
 * @property isCompleted Indicador de processo de avaliação finalizado.
 */
data class ReviewsStoreUiState(
    val title: String = "Avaliação na Google Play Store",
    val starRating: Int = 0,
    val hasVoted: Boolean = false,
    val feedbackComment: String = "",
    val redirectTarget: ReviewRedirectTarget = ReviewRedirectTarget.NONE,
    val isCompleted: Boolean = false
)

/**
 * ViewModel responsável pela triagem de satisfação e redirecionamento estratégico para a Play Store.
 */
@HiltViewModel
class ReviewsStoreViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsStoreUiState())

    /**
     * Fluxo de estado observável com as etapas da avaliação.
     */
    val uiState: StateFlow<ReviewsStoreUiState> = _uiState.asStateFlow()

    /**
     * Processa a seleção de estrelas efetuada pelo usuário e determina o fluxo de redirecionamento.
     *
     * @param stars Quantidade de estrelas (1 a 5).
     */
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

    /**
     * Atualiza o comentário de sugestão interna para avaliações críticas.
     *
     * @param comment Texto fornecido pelo cliente.
     */
    fun updateFeedbackComment(comment: String) {
        _uiState.update { it.copy(feedbackComment = comment) }
    }

    /**
     * Marca o fluxo de avaliação como concluído com sucesso.
     */
    fun completeReview() {
        _uiState.update { it.copy(isCompleted = true) }
    }

    /**
     * Reinicializa o estado da avaliação para os valores padrão.
     */
    fun reset() {
        _uiState.update { ReviewsStoreUiState() }
    }
}