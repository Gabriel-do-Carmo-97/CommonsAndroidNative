package br.com.wgc.profile.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.design_system.factories.profile.templates.SettingsHubScreenTemplate
import br.com.wgc.design_system.factories.settings.WgcSettingsScreenFactory
import br.com.wgc.design_system.factories.settings.WgcSettingsVariant

/**
 * Tela corporativa de Perfil e Gestão de Conta do usuário autenticado.
 *
 * Integra a [WgcSettingsScreenFactory] do `DesignSystemAndroid 0.25.0` para apresentar dados cadastrais,
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
 *         settingsType = WgcSettingsVariant.STANDARD
 *     )
 * }
 *
 * // 2. Uso com navegação e custom callbacks:
 * ProfileScreen(
 *     settingsType = WgcSettingsVariant.PREFERENCES,
 *     onBackClick = { navegarParaHome() },
 *     onLogoutClick = { deslogarUsuario() }
 * )
 * ```
 *
 * @param modifier Modificador de layout Compose aplicado ao contêiner raiz.
 * @param settingsType Variante da central de configurações via [WgcSettingsVariant]. O padrão é [WgcSettingsVariant.STANDARD].
 * @param viewModel ViewModel injetado via Hilt para controle reativo de dados de perfil e logout.
 * @param onBackClick Callback opcional disparado ao clicar no botão de retorno.
 * @param onLogoutClick Callback opcional disparado quando o usuário solicita encerramento da sessão.
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    settingsType: WgcSettingsVariant = WgcSettingsVariant.STANDARD,
    viewModel: ProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = { viewModel.onLogoutClick() }
) {
    if (settingsType == WgcSettingsVariant.STANDARD) {
        SettingsHubScreenTemplate(
            viewModel = viewModel
        )
    } else {
        WgcSettingsScreenFactory(
            modifier = modifier,
            variant = settingsType,
            onBackClick = onBackClick,
            onLogoutClick = onLogoutClick
        )
    }
}