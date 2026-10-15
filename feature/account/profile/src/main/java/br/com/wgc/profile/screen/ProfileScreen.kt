package br.com.wgc.profile.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.design_system.templates.factories.WgcSettingsHubFactory
import br.com.wgc.design_system.templates.factories.WgcSettingsHubType
import br.com.wgc.design_system.templates.screens.profile.SettingsHubScreenTemplate

/**
 * Tela corporativa de Perfil e Gestão de Conta do usuário autenticado.
 *
 * Integra a [WgcSettingsHubFactory] do `DesignSystemAndroid 0.24.1` para apresentar dados cadastrais,
 * avatar, preferências visuais, notificações e encerramento de sessão seguro.
 *
 * ### 📌 Quando Usar:
 * Utilize esta tela como área principal do usuário logado na aba "Perfil" ou "Minha Conta",
 * centralizando dados pessoais e acessos a configurações de segurança e LGPD.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Uso padrão via ViewModel injetado:
 * @Composable
 * fun AbaPerfil() {
 *     ProfileScreen(
 *         settingsType = WgcSettingsHubType.STANDARD
 *     )
 * }
 *
 * // 2. Uso com navegação e custom callbacks:
 * ProfileScreen(
 *     settingsType = WgcSettingsHubType.PREFERENCES,
 *     onBackClick = { navegarParaHome() },
 *     onLogoutClick = { deslogarUsuario() }
 * )
 * ```
 *
 * @param modifier Modificador de layout Compose aplicado ao contêiner raiz.
 * @param settingsType Variante da central de configurações via [WgcSettingsHubType]. O padrão é [WgcSettingsHubType.STANDARD].
 * @param viewModel ViewModel injetado via Hilt para controle reativo de dados de perfil e logout.
 * @param onBackClick Callback opcional disparado ao clicar no botão de retorno.
 * @param onLogoutClick Callback opcional disparado quando o usuário solicita encerramento da sessão.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    settingsType: WgcSettingsHubType = WgcSettingsHubType.STANDARD,
    viewModel: ProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = { viewModel.onLogoutClick() }
) {
    if (settingsType == WgcSettingsHubType.STANDARD) {
        SettingsHubScreenTemplate(
            viewModel = viewModel
        )
    } else {
        WgcSettingsHubFactory(
            modifier = modifier,
            type = settingsType,
            onBackClick = onBackClick,
            onLogoutClick = onLogoutClick
        )
    }
}