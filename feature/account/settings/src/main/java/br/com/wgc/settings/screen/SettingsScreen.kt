package br.com.wgc.settings.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.ds_templates.screens.profile.SettingsHubScreenTemplate

/**
 * Tela de Configurações e Preferências gerais do aplicativo.
 *
 * @param modifier Modificador de layout Compose.
 * @param viewModel ViewModel de gerenciamento das configurações.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsHubScreenTemplate(
        viewModel = viewModel
    )
}