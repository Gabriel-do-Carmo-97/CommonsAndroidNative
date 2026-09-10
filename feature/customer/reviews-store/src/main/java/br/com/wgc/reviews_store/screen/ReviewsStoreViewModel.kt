package br.com.wgc.reviews_store.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Destino para redirecionamento apÃ³s a seleÃ§Ã£o da quantidade de estrelas pelo usuÃ¡rio.
 */
enum class ReviewRedirectTarget {
    /** Nenhuma aÃ§Ã£o selecionada ainda. */
    NONE,
    /** Redirecionamento para a pÃ¡gina oficial do app na Google Play Store (4 ou 5 estrelas). */
    PLAY_STORE,
    /** Direcionamento para coleta de sugestÃ£o interna de melhoria (1 a 3 estrelas). */
    INTERNAL_FEEDBACK
}

/**
 * Estado imutÃ¡vel do fluxo de avaliaÃ§Ã£o do app na Play Store.
 *
 * @property title TÃ­tulo do cabeÃ§alho da avaliaÃ§Ã£o.
 * @property starRating Quantidade de estrelas selecionada (0 a 5).
 * @property hasVoted Sinalizador indicando se o usuÃ¡rio jÃ¡ selecionou uma quantidade de estrelas.
 * @property feedbackComment ComentÃ¡rio textual interno para notas baixas.
 * @property redirectTarget Destino da triagem da avaliaÃ§Ã£o.
 * @property isCompleted Indicador de processo de avaliaÃ§Ã£o finalizado.
 */
data class ReviewsStoreUiState(
    val title: String = "AvaliaÃ§Ã£o na Google Play Store",
    val starRating: Int = 0,
    val hasVoted: Boolean = false,
    val feedbackComment: String = "",
    val redirectTarget: ReviewRedirectTarget = ReviewRedirectTarget.NONE,
    val isCompleted: Boolean = false
)

/**
 * ViewModel responsÃ¡vel pela triagem de satisfaÃ§Ã£o e redirecionamento estratÃ©gico para a Play Store.
 */
@HiltViewModel
class ReviewsStoreViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsStoreUiState())

    /**
     * Fluxo de estado observÃ¡vel com as etapas da avaliaÃ§Ã£o.
     */
    val uiState: StateFlow<ReviewsStoreUiState> = _uiState.asStateFlow()

    /**
     * Processa a seleÃ§Ã£o de estrelas efetuada pelo usuÃ¡rio e determina o fluxo de redirecionamento.
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
     * Atualiza o comentÃ¡rio de sugestÃ£o interna para avaliaÃ§Ãµes crÃ­ticas.
     *
     * @param comment Texto fornecido pelo cliente.
     */
    fun updateFeedbackComment(comment: String) {
        _uiState.update { it.copy(feedbackComment = comment) }
    }

    /**
     * Marca o fluxo de avaliaÃ§Ã£o como concluÃ­do com sucesso.
     */
    fun completeReview() {
        _uiState.update { it.copy(isCompleted = true) }
    }

    /**
     * Reinicializa o estado da avaliaÃ§Ã£o para os valores padrÃ£o.
     */
    fun reset() {
        _uiState.update { ReviewsStoreUiState() }
    }
}