package br.com.gds.payment.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.home.fintech.FintechHomeScreenTemplate

@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    FintechHomeScreenTemplate(
        viewModel = viewModel
    )
}
