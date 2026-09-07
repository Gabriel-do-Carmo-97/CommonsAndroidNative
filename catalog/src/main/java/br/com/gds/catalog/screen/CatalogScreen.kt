package br.com.gds.catalog.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.home.ecommerce.EcommerceHomeScreenTemplate

@Composable
fun CatalogScreen(
    modifier: Modifier = Modifier,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    EcommerceHomeScreenTemplate(
        viewModel = viewModel
    )
}
