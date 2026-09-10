package br.com.wgc.authentication.registerUser

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.register.user.screen.RegisterUserScreenTemplate

/**
 * Tela de cadastro inicial de usuÃ¡rio com dados pessoais e aceitaÃ§Ã£o de termos.
 *
 * @param modifier Modificador de layout Compose.
 * @param viewModel ViewModel de gerenciamento do cadastro.
 */
@Composable
fun RegisterUserScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterUserViewModel = hiltViewModel()
) = RegisterUserScreenTemplate(
    modifier = modifier,
    viewModel = viewModel
)