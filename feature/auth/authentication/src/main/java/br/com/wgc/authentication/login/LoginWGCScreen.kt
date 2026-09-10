package br.com.wgc.authentication.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.login.screen.LoginScreenTemplate

/**
 * Ponto de entrada Composable da tela principal de login e identificaÃ§Ã£o de usuÃ¡rio.
 *
 * Fornece interface com suporte a credenciais tradicionais (e-mail e senha) e autenticaÃ§Ã£o biomÃ©trica,
 * integrada aos temas e componentes do Design System.
 *
 * @param modifier Modificador de layout Jetpack Compose aplicado ao contÃªiner raiz.
 * @param viewModel ViewModel responsÃ¡vel pelo gerenciamento de estado e ciclo de autenticaÃ§Ã£o.
 */
@Composable
fun LoginWGCScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginWGCViewModel = hiltViewModel()
) = LoginScreenTemplate(modifier = modifier, viewModel = viewModel)