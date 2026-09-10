package br.com.wgc.profile.screen

import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.session.AuthSessionState
import br.com.wgc.authentication.session.WgcAuthManager
import br.com.wgc.ds_templates.screens.profile.BaseSettingsHubViewModel
import br.com.wgc.ds_templates.screens.profile.SettingsHubUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsÃ¡vel pelo gerenciamento de dados de perfil e preferÃªncias do usuÃ¡rio.
 *
 * @param authManager Gerenciador de sessÃ£o corporativo injetado via Hilt.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authManager: WgcAuthManager
) : BaseSettingsHubViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsHubUiState(
            userName = authManager.currentUser?.displayName ?: "UsuÃ¡rio WGC",
            email = authManager.currentUser?.email ?: "usuario@wgc.com.br",
            notificationsEnabled = true,
            darkModeEnabled = false
        )
    )
    override val uiState: StateFlow<SettingsHubUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authManager.sessionState.collect { session ->
                when (session) {
                    is AuthSessionState.Authenticated -> {
                        _uiState.update {
                            it.copy(
                                userName = session.user.displayName ?: "UsuÃ¡rio WGC",
                                email = session.user.email ?: "usuario@wgc.com.br"
                            )
                        }
                    }
                    is AuthSessionState.Unauthenticated -> {
                        _uiState.update {
                            it.copy(
                                userName = "Visitante",
                                email = "NÃ£o autenticado"
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    /**
     * Alterna o recebimento de notificaÃ§Ãµes push no dispositivo.
     *
     * @param enabled Indica se as notificaÃ§Ãµes estÃ£o ativadas.
     */
    override fun onToggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    /**
     * Alterna entre o tema escuro e claro no aplicativo.
     *
     * @param enabled Indica se o tema escuro estÃ¡ ativado.
     */
    override fun onToggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(darkModeEnabled = enabled) }
    }

    /**
     * Realiza o encerramento da sessÃ£o ativa do usuÃ¡rio.
     */
    override fun onLogoutClick() {
        viewModelScope.launch {
            authManager.logout()
        }
    }
}