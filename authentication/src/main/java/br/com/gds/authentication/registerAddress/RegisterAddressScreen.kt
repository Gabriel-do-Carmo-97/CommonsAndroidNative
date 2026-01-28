package br.com.gds.authentication.registerAddress

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.register.address.screen.RegisterAddressScreenTemplate

@Composable
fun RegisterAddressScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterAddressViewModel = hiltViewModel()
) = RegisterAddressScreenTemplate(
    modifier = modifier,
    viewModel = viewModel
)