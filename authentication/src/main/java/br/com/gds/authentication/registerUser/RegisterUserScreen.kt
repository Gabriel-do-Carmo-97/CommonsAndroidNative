package br.com.gds.authentication.registerUser

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.register.user.screen.RegisterUserScreenTemplate

@Composable
fun RegisterUserScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterUserViewModel = hiltViewModel()
) = RegisterUserScreenTemplate(
    modifier = modifier,
    viewModel = viewModel
)