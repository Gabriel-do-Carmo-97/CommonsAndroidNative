package br.com.wgc.authentication.forgotPassword

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.resetpassword.screen.ResetPasswordScreenTemplate

/**
 * Tela de recuperaÃ§Ã£o de senha corporativa integrada ao OmniBackend.
 *
 * ProvÃª formulÃ¡rio padronizado de redefiniÃ§Ã£o de credenciais de acesso via e-mail ou SMS,
 * delegando renderizaÃ§Ã£o e estados visuais ao template do Design System.
 *
 * @param modifier Modificador de layout Jetpack Compose aplicado ao contÃªiner raiz.
 * @param viewModel ViewModel responsÃ¡vel pelo gerenciamento de estado e chamadas de recuperaÃ§Ã£o.
 */
@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) = ResetPasswordScreenTemplate(modifier = modifier, viewModel = viewModel)