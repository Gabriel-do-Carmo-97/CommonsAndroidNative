package br.com.wgc.profile.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.profile.SettingsHubScreenTemplate

/**
 * Tela de Perfil e GestÃ£o de Conta do usuÃ¡rio autenticado.
 *
 * Apresenta dados pessoais, avatar, alternÃ¢ncias de notificaÃ§Ãµes e temas,
 * bem como a aÃ§Ã£o de encerramento seguro de sessÃ£o.
 *
 * @param modifier Modificador de layout Compose.
 * @param viewModel ViewModel de gerenciamento do perfil e sessÃ£o do usuÃ¡rio.
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