package br.com.wgc.authentication.registerAddress

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.register.address.screen.RegisterAddressScreenTemplate

/**
 * Tela de cadastro de endereço residencial ou comercial do usuário.
 *
 * @param modifier Modificador de layout aplicado ao contêiner.
 * @param viewModel ViewModel de controle do formulário de endereço.
 */
@Composable
fun RegisterAddressScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterAddressViewModel = hiltViewModel()
) = RegisterAddressScreenTemplate(
    modifier = modifier,
    viewModel = viewModel
)