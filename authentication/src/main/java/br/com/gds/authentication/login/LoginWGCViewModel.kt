package br.com.gds.authentication.login

import androidx.lifecycle.viewModelScope
import br.com.gds.authentication.navigation.AuthNavDestinations
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.ds_templates.screens.login.viewmodel.BaseLoginScreenTemplateViewModel
import br.com.wgc.firebase_sdk.domain.usecase.LoginUseCase
import br.com.wgc.firebase_sdk.utils.UseCaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class LoginWGCViewModel @Inject constructor(
    private val useCase: LoginUseCase,
    private val dataStore: DataStorePreferencesCore
) : BaseLoginScreenTemplateViewModel() {
    private val _navigationEvent = Channel<AuthNavDestinations.LoginScreen>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {

    }
    override fun onLoginClick() {
        viewModelScope.launch {
             val result = useCase(
                 email = uiState.value.email,
                 password = uiState.value.password
             ).first()
            when(result){
                is UseCaseResult.Failure -> TODO()
                is UseCaseResult.Loading -> TODO()
                is UseCaseResult.Success<*> -> TODO()
            }
        }
    }

    override fun onRegisterClick() {
        viewModelScope.launch {
            _navigationEvent.trySend(AuthNavDestinations.LoginScreen.RegisterUser)
        }
    }

    override fun onForgotPasswordClick() {
        viewModelScope.launch {
            _navigationEvent.trySend(AuthNavDestinations.LoginScreen.ForgotPassword)
        }
    }

    override fun onRememberMeCheckedChange(isChecked: Boolean) {
        super.onRememberMeCheckedChange(isChecked)
        viewModelScope.launch {
        }
    }

}