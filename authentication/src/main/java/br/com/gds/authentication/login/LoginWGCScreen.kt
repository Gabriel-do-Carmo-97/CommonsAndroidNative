package br.com.gds.authentication.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.login.screen.LoginScreenTemplate

@Composable
fun LoginWGCScreen(
    modifier: Modifier,
    viewModel: LoginWGCViewModel = hiltViewModel()
) {
    LoginScreenTemplate(modifier = modifier, viewModel = viewModel)
}