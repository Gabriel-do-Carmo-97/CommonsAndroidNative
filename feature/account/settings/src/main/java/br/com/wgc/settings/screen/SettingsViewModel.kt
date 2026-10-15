package br.com.wgc.settings.screen

import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.session.AuthSessionState
import br.com.wgc.authentication.session.WgcAuthManager
import br.com.wgc.core.security.EncryptedSharedPreferencesCore
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
 * ViewModel corporativo para controle de preferências de configuração da conta, notificações e tema.
 *
 * Conecta-se de forma reativa ao [WgcAuthManager] para refletir o estado do usuário e persiste
 * preferências sensíveis e flags de segurança no hardware seguro via [EncryptedSharedPreferencesCore].
 *
 * ### 📌 Quando Usar:
 * Utilize este ViewModel ao exibir a tela de configurações do aplicativo (`SettingsScreen`),
 * permitindo ao usuário customizar parâmetros visuais, notificações e encerrar a sessão com segurança.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @Composable
 * fun TelaDeConfiguracoes(
 *     viewModel: SettingsViewModel = hiltViewModel()
 * ) {
 *     val state by viewModel.uiState.collectAsState()
 *
 *     SettingsHubScreenTemplate(viewModel = viewModel)
 * }
 * ```
 *
 * @param authManager Fachada pública do gerenciador de sessão corporativo provido via Hilt.
 * @param encryptedStorage Mecanismo de persistência criptografada em disco provido pelo CoreAndroidNative.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authManager: WgcAuthManager,
    private val encryptedStorage: EncryptedSharedPreferencesCore
) : BaseSettingsHubViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsHubUiState(
            userName = authManager.currentUser?.displayName ?: "Usuário WGC",
            email = authManager.currentUser?.email ?: "usuario@wgc.com.br",
            notificationsEnabled = encryptedStorage.getBoolean(PREF_NOTIFICATIONS_KEY, true),
            darkModeEnabled = encryptedStorage.getBoolean(PREF_DARK_MODE_KEY, false)
        )
    )

    /**
     * Fluxo reativo contendo o estado atual das opções de configuração do aplicativo.
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
     * Alterna a preferência de notificações push no dispositivo.
     *
     * ### 📌 Quando Usar:
     * Acionado ao alternar o switch de notificações nas configurações.
     *
     * @param enabled Indica se notificações estão habilitadas.
     */
    override fun onToggleNotifications(enabled: Boolean) {
        encryptedStorage.saveBoolean(PREF_NOTIFICATIONS_KEY, enabled)
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    /**
     * Alterna a preferência de tema escuro.
     *
     * ### 📌 Quando Usar:
     * Acionado ao alternar a chave de Dark Mode.
     *
     * @param enabled Indica se o tema escuro está ativado.
     */
    override fun onToggleDarkMode(enabled: Boolean) {
        encryptedStorage.saveBoolean(PREF_DARK_MODE_KEY, enabled)
        _uiState.update { it.copy(darkModeEnabled = enabled) }
    }

    /**
     * Encerra a sessão ativa do usuário no OmniBackend e limpa credenciais locais.
     *
     * ### 📌 Quando Usar:
     * Acionado ao clicar no botão "Sair" da central de configurações.
     */
    override fun onLogoutClick() {
        viewModelScope.launch {
            authManager.logout()
        }
    }

    companion object {
        /** Chave segura para armazenamento da preferência de notificações. */
        const val PREF_NOTIFICATIONS_KEY = "pref_notifications_enabled"

        /** Chave segura para armazenamento da preferência de tema escuro. */
        const val PREF_DARK_MODE_KEY = "pref_dark_mode_enabled"
    }
}