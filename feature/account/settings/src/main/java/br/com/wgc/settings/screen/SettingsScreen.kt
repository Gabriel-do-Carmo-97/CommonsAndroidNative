package br.com.wgc.settings.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.wgc.design_system.factories.profile.templates.SettingsHubScreenTemplate
import br.com.wgc.design_system.factories.settings.WgcSettingsScreenFactory
import br.com.wgc.design_system.factories.settings.WgcSettingsVariant

/**
 * Tela corporativa de Configurações, Segurança e Preferências gerais do aplicativo.
 *
 * Utiliza a [WgcSettingsScreenFactory] do `DesignSystemAndroid 0.25.0` para gerenciar toggles de notificações,
 * parâmetros de segurança biométrica, modo escuro e links de privacidade.
 *
 * ### 📌 Quando Usar:
 * Utilize esta tela no menu de ajustes do app, permitindo ao usuário customizar a experiência
 * de uso e gerenciar consentimentos de privacidade (LGPD).
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * // 1. Uso padrão via ViewModel injetado:
 * @Composable
 * fun TelaDeAjustes() {
 *     SettingsScreen(
 *         settingsType = WgcSettingsVariant.SECURITY
 *     )
 * }
 *
 * // 2. Uso com callbacks de navegação:
 * SettingsScreen(
 *     settingsType = WgcSettingsVariant.PREFERENCES,
 *     onBackClick = { voltarParaTelaAnterior() }
 * )
 * ```
 *
 * @param modifier Modificador de layout Compose aplicado ao contêiner raiz.
 * @param settingsType Variante temática da tela de configurações via [WgcSettingsVariant]. O padrão é [WgcSettingsVariant.STANDARD].
 * @param viewModel ViewModel injetado via Hilt para controle e persistência de preferências do aplicativo.
 * @param onBackClick Callback opcional disparado ao clicar no botão de retorno.
 * @param onLogoutClick Callback opcional disparado caso o usuário solicite encerramento de sessão.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsType: WgcSettingsVariant = WgcSettingsVariant.STANDARD,
    viewModel: SettingsViewModel = hiltViewModel(),
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