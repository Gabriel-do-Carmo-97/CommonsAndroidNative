package br.com.wgc.profile.screen

import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.session.AuthSessionState
import br.com.wgc.authentication.session.WgcAuthManager
import br.com.wgc.design_system.templates.screens.profile.BaseSettingsHubViewModel
import br.com.wgc.design_system.templates.screens.profile.SettingsHubUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel corporativo para controle de perfil, dados cadastrais e preferências do usuário autenticado.
 *
 * Observa em tempo real o [WgcAuthManager] do OmniBackend 1.2.0, refletindo atualizações
 * de nome, e-mail e permitindo o encerramento seguro da sessão.
 *
 * ### 📌 Quando Usar:
 * Utilize este ViewModel ao exibir a tela de perfil do usuário (`ProfileScreen`), gerenciando
 * a sincronização entre a identidade corporativa em nuvem e a interface Jetpack Compose.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun AbaMeuPerfil(
 *     viewModel: ProfileViewModel = hiltViewModel()
 * ) {
 *     val state by viewModel.uiState.collectAsState()
 *
 *     Text("Bem-vindo, ${state.userName}")
 *     SettingsHubScreenTemplate(viewModel = viewModel)
 * }
 * ```
 *
 * @param authManager Fachada pública do gerenciador de sessão corporativo provido via Hilt.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authManager: WgcAuthManager
) : BaseSettingsHubViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsHubUiState(
            userName = authManager.currentUser?.displayName ?: "Usuário WGC",
            email = authManager.currentUser?.email ?: "usuario@wgc.com.br",
            notificationsEnabled = true,
            darkModeEnabled = false
        )
    )

    /**
     * Fluxo reativo com as informações de perfil e preferências visuais do usuário.
     */
    override val uiState: StateFlow<SettingsHubUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authManager.sessionState.collect { session ->
                when (session) {
                    is AuthSessionState.Authenticated -> {
                        _uiState.update {
                            it.copy(
                                userName = session.user.displayName ?: "Usuário WGC",
                                email = session.user.email ?: "usuario@wgc.com.br"
                            )
                        }
                    }
                    is AuthSessionState.Unauthenticated -> {
                        _uiState.update {
                            it.copy(
                                userName = "Visitante",
                                email = "Não autenticado"
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    /**
     * Alterna o recebimento de notificações push no dispositivo.
     *
     * ### 📌 Quando Usar:
     * Acionado pelo usuário ao clicar no switch de notificações nas configurações de perfil.
     *
     * @param enabled Indica se as notificações estão ativadas.
     */
    override fun onToggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    /**
     * Alterna entre o tema escuro e claro no aplicativo.
     *
     * ### 📌 Quando Usar:
     * Acionado pelo usuário ao alternar a chave de tema (Dark Mode).
     *
     * @param enabled Indica se o tema escuro está ativado.
     */
    override fun onToggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(darkModeEnabled = enabled) }
    }

    /**
     * Realiza o encerramento da sessão ativa do usuário no OmniBackend e limpa dados locais.
     *
     * ### 📌 Quando Usar:
     * Acionado quando o usuário confirma a ação "Sair da conta".
     */
    override fun onLogoutClick() {
        viewModelScope.launch {
            authManager.logout()
        }
    }
}