package br.com.wgc.authentication.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.login.screen.LoginScreenTemplate

/**
 * Ponto de entrada Composable da tela principal de login e identificação de usuário.
 *
 * Fornece interface com suporte a credenciais tradicionais (e-mail e senha) e autenticação biométrica,
 * integrada aos temas e componentes do Design System.
 *
 * @param modifier Modificador de layout Jetpack Compose aplicado ao contêiner raiz.
 * @param viewModel ViewModel responsável pelo gerenciamento de estado e ciclo de autenticação.
 */
@Composable
fun LoginWGCScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginWGCViewModel = hiltViewModel()
) = LoginScreenTemplate(modifier = modifier, viewModel = viewModel)