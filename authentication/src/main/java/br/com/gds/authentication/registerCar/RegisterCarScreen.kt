package br.com.gds.authentication.registerCar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.register.car.screen.RegisterCarScreenTemplate

@Composable
fun RegisterCarScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterCarViewModel = hiltViewModel()
) = RegisterCarScreenTemplate(
    modifier = modifier,
    viewModel = viewModel
)