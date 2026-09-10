package br.com.wgc.promotions.screen

import br.com.wgc.ds_templates.screens.social.BaseInstagramStoryViewerViewModel
import br.com.wgc.ds_templates.screens.social.InstagramStoryViewerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel responsÃ¡vel pela navegaÃ§Ã£o temporal e reproduÃ§Ã£o de mÃ­dias promocionais em Stories.
 */
@HiltViewModel
class PromotionsViewModel @Inject constructor() : BaseInstagramStoryViewerViewModel() {

    private val _uiState = MutableStateFlow(
        InstagramStoryViewerUiState(
            userName = "Ofertas RelÃ¢mpago WGC",
            timeAgo = "hÃ¡ 15 minutos",
            segmentCount = 4,
            activeSegmentIndex = 0,
            mediaUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591",
            isLiked = false,
            replyText = ""
        )
    )
    override val uiState: StateFlow<InstagramStoryViewerUiState> = _uiState.asStateFlow()

    /** Alterna a marcaÃ§Ã£o de curtida na promoÃ§Ã£o ativa. */
    override fun onToggleLike() {
        _uiState.update { it.copy(isLiked = !it.isLiked) }
    }

    /** Atualiza o texto de resposta ou mensagem enviada sobre a promoÃ§Ã£o. */
    override fun onReplyChange(text: String) {
        _uiState.update { it.copy(replyText = text) }
    }

    /** Encerra a visualizaÃ§Ã£o do carrossel promocional. */
    override fun onCloseClick() {
        // Fechar visualizador de stories
    }

    /** AvanÃ§a para o prÃ³ximo segmento promocional. */
    fun nextStory() {
        _uiState.update {
            val nextIndex = (it.activeSegmentIndex + 1).coerceAtMost(it.segmentCount - 1)
            it.copy(activeSegmentIndex = nextIndex)
        }
    }

    /** Retorna para o segmento promocional anterior. */
    fun previousStory() {
        _uiState.update {
            val prevIndex = (it.activeSegmentIndex - 1).coerceAtLeast(0)
            it.copy(activeSegmentIndex = prevIndex)
        }
    }
}