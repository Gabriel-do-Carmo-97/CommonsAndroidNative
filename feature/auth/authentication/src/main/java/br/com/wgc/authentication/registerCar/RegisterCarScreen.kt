package br.com.wgc.authentication.registerCar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.register.car.screen.RegisterCarScreenTemplate

/**
 * Tela de cadastro de veÃ­culo do motorista ou entregador parceiro.
 *
 * @param modifier Modificador de layout Compose.
 * @param viewModel ViewModel de controle do cadastro veicular.
 */
@Composable
fun RegisterCarScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterCarViewModel = hiltViewModel()
) = RegisterCarScreenTemplate(
    modifier = modifier,
    viewModel = viewModel
)