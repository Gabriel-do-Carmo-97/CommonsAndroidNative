package br.com.wgc.authentication.forgotPassword

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.resetpassword.screen.ResetPasswordScreenTemplate

/**
 * Tela de recuperação de senha corporativa integrada ao OmniBackend.
 *
 * Provê formulário padronizado de redefinição de credenciais de acesso via e-mail ou SMS,
 * delegando renderização e estados visuais ao template do Design System.
 *
 * @param modifier Modificador de layout Jetpack Compose aplicado ao contêiner raiz.
 * @param viewModel ViewModel responsável pelo gerenciamento de estado e chamadas de recuperação.
 */
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) = ResetPasswordScreenTemplate(modifier = modifier, viewModel = viewModel)