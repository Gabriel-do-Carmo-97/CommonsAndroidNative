package br.com.gds.authentication.login

import androidx.lifecycle.viewModelScope
import br.com.gds.authentication.navigation.AuthNavDestinations
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.ds_templates.screens.login.viewmodel.BaseLoginScreenTemplateViewModel
import br.wgc.omnibackend.core.repository.AuthRepository
import br.wgc.omnibackend.core.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginWGCViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStore: DataStorePreferencesCore
) : BaseLoginScreenTemplateViewModel() {
    private val _navigationEvent = Channel<AuthNavDestinations.LoginScreen>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    override fun onLoginClick() {
        viewModelScope.launch {
            val result = authRepository.login(
                email = uiState.value.email,
                pass = uiState.value.password
            )
            when (result) {
                is DataResult.Success -> {
                    _navigationEvent.send(
                        AuthNavDestinations.LoginScreen.LoginSuccess(email = uiState.value.email)
                    )
                }
                is DataResult.Failure -> {
                    // Fallback seguro evitando que o app quebre por falta de tratamento
                }
            }
        }
    }

    override fun onRegisterClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.LoginScreen.RegisterUser)
        }
    }

    override fun onForgotPasswordClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.LoginScreen.ForgotPassword)
        }
    }

    override fun onRememberMeCheckedChange(isChecked: Boolean) {
        super.onRememberMeCheckedChange(isChecked)
    }
}