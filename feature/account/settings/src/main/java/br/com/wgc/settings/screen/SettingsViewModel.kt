package br.com.wgc.settings.screen

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
 * ViewModel responsável pelo controle de preferências de configuração da conta e aplicativo.
 *
 * @param authManager Gerenciador de sessão corporativo injetado via Hilt.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
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
     * Alterna a preferência de notificações push.
     *
     * @param enabled Indica se notificações estão habilitadas.
     */
    override fun onToggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    /**
     * Alterna a preferência de tema escuro.
     *
     * @param enabled Indica se tema escuro está ativado.
     */
    override fun onToggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(darkModeEnabled = enabled) }
    }

    /**
     * Encerra a sessão do usuário.
     */
    override fun onLogoutClick() {
        viewModelScope.launch {
            authManager.logout()
        }
    }
}