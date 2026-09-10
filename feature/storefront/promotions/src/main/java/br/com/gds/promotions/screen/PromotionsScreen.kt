package br.com.gds.promotions.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.social.InstagramStoryViewerScreenTemplate

@Composable
fun PromotionsScreen(
    modifier: Modifier = Modifier,
    viewModel: PromotionsViewModel = hiltViewModel()
) {
    InstagramStoryViewerScreenTemplate(
        viewModel = viewModel
    )
}
