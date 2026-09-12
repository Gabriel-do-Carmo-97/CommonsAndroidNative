package br.com.wgc.profile.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.profile.SettingsHubScreenTemplate

/**
 * Tela de Perfil e Gestão de Conta do usuário autenticado.
 *
 * Apresenta dados pessoais, avatar, alternâncias de notificações e temas,
 * bem como a ação de encerramento seguro de sessão.
 *
 * @param modifier Modificador de layout Compose.
 * @param viewModel ViewModel de gerenciamento do perfil e sessão do usuário.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    SettingsHubScreenTemplate(
        viewModel = viewModel
    )
}