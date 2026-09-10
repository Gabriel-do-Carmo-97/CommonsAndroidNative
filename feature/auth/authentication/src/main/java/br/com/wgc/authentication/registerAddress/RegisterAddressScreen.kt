package br.com.wgc.authentication.registerAddress

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.register.address.screen.RegisterAddressScreenTemplate

/**
 * Tela de cadastro de endereÃ§o residencial ou comercial do usuÃ¡rio.
 *
 * @param modifier Modificador de layout aplicado ao contÃªiner.
 * @param viewModel ViewModel de controle do formulÃ¡rio de endereÃ§o.
 */
@Composable
fun RegisterAddressScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterAddressViewModel = hiltViewModel()
) = RegisterAddressScreenTemplate(
    modifier = modifier,
    viewModel = viewModel
)