package br.com.gds.authentication.forgotPassword

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.resetpassword.screen.ResetPasswordScreenTemplate

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) = ResetPasswordScreenTemplate(modifier = modifier, viewModel = viewModel)