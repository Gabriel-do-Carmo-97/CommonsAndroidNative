package br.com.gds.promotions.screen

import br.com.wgc.ds_templates.screens.social.BaseInstagramStoryViewerViewModel
import br.com.wgc.ds_templates.screens.social.InstagramStoryViewerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PromotionsViewModel @Inject constructor() : BaseInstagramStoryViewerViewModel() {

    private val _uiState = MutableStateFlow(
        InstagramStoryViewerUiState(
            userName = "Ofertas Relâmpago WGC",
            timeAgo = "há 15 minutos",
            segmentCount = 4,
            activeSegmentIndex = 0,
            mediaUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591",
            isLiked = false,
            replyText = ""
        )
    )
    override val uiState: StateFlow<InstagramStoryViewerUiState> = _uiState.asStateFlow()

    override fun onToggleLike() {
        _uiState.update { it.copy(isLiked = !it.isLiked) }
    }

    override fun onReplyChange(text: String) {
        _uiState.update { it.copy(replyText = text) }
    }

    override fun onCloseClick() {
        // Fechar visualizador de stories
    }

    fun nextStory() {
        _uiState.update {
            val nextIndex = (it.activeSegmentIndex + 1).coerceAtMost(it.segmentCount - 1)
            it.copy(activeSegmentIndex = nextIndex)
        }
    }

    fun previousStory() {
        _uiState.update {
            val prevIndex = (it.activeSegmentIndex - 1).coerceAtLeast(0)
            it.copy(activeSegmentIndex = prevIndex)
        }
    }
}
