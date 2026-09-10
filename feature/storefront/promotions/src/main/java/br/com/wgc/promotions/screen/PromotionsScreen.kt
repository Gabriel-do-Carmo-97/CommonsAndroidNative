package br.com.wgc.promotions.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.social.InstagramStoryViewerScreenTemplate

/**
 * Tela de visualizaÃ§Ã£o de promoÃ§Ãµes e ofertas em formato dinÃ¢mico de Stories.
 *
 * @param modifier Modificador de layout Compose.
 * @param viewModel ViewModel de controle dos segmentos e mÃ­dias promocionais.
 */
@Composable
fun PromotionsScreen(
    modifier: Modifier = Modifier,
    viewModel: PromotionsViewModel = hiltViewModel()
) {
    InstagramStoryViewerScreenTemplate(
        viewModel = viewModel
    )
}