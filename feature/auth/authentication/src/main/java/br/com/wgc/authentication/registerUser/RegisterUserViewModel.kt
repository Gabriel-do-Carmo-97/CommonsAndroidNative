package br.com.wgc.authentication.registerUser

import androidx.lifecycle.viewModelScope
import br.com.wgc.authentication.navigation.AuthNavDestinations
import br.com.wgc.authentication.utils.toUserFriendlyMessage
import br.com.wgc.core.validators.isValidEmail
import br.com.wgc.ds_templates.screens.register.user.viewmodel.BaseRegisterUserTemplateViewModel
import br.wgc.omnibackend.core.model.auth.NewUser
import br.wgc.omnibackend.firebase.domain.usecase.UserRegisterUseCase
import br.wgc.omnibackend.firebase.utils.UseCaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    private val userRegisterUseCase: UserRegisterUseCase
) : BaseRegisterUserTemplateViewModel() {

    private val _navigationEvent = Channel<AuthNavDestinations.RegisterUserScreen>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    override fun onRegisterClick() {
        val state = uiState.value

        if (!state.acceptedTerms) {
            updateState { it.copy(generalError = "Você deve aceitar os termos para continuar") }
            return
        }

        if (!state.email.isValidEmail()) {
            updateState { it.copy(emailError = "Informe um e-mail válido") }
            return
        }

        val newUser = NewUser(
            name = state.name.trim(),
            lastName = state.lastName.trim(),
            email = state.email.trim(),
            password = state.password,
            isClient = true
        )

        viewModelScope.launch {
            userRegisterUseCase(newUser).collect { result ->
                when (result) {
                    is UseCaseResult.Loading -> {
                        updateState { it.copy(isLoading = true, generalError = null) }
                    }
                    is UseCaseResult.Success -> {
                        updateState { it.copy(isLoading = false, generalError = null) }
                        _navigationEvent.send(AuthNavDestinations.RegisterUserScreen.RegisterAddress)
                    }
                    is UseCaseResult.Failure -> {
                        updateState {
                            it.copy(
                                isLoading = false,
                                generalError = result.error.toUserFriendlyMessage()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onLoginClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterUserScreen.Login)
        }
    }

    override fun onTermsClick() {
        // Callback para exibir modal de termos ou navegação futura
    }

    override fun onBackClick() {
        viewModelScope.launch {
            _navigationEvent.send(AuthNavDestinations.RegisterUserScreen.Login)
        }
    }
}