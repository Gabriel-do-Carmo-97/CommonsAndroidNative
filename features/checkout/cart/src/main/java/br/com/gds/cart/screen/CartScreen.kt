package br.com.gds.cart.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.cart.StandardCartScreenTemplate

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel()
) {
    StandardCartScreenTemplate(
        viewModel = viewModel
    )
}
