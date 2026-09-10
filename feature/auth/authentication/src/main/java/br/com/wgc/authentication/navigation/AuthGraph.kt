package br.com.wgc.authentication.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import br.com.wgc.authentication.config.AuthConfig
import br.com.wgc.authentication.forgotPassword.ForgotPasswordScreen
import br.com.wgc.authentication.forgotPassword.ForgotPasswordViewModel
import br.com.wgc.authentication.login.LoginWGCScreen
import br.com.wgc.authentication.login.LoginWGCViewModel
import br.com.wgc.authentication.registerAddress.RegisterAddressScreen
import br.com.wgc.authentication.registerAddress.RegisterAddressViewModel
import br.com.wgc.authentication.registerCar.RegisterCarScreen
import br.com.wgc.authentication.registerCar.RegisterCarViewModel
import br.com.wgc.authentication.registerUser.RegisterUserScreen
import br.com.wgc.authentication.registerUser.RegisterUserViewModel

/**
 * Extensão que registra as rotas do fluxo de autenticação diretamente no NavGraphBuilder
 * do aplicativo consumidor, respeitando o [AuthConfig] fornecido.
 */
fun NavGraphBuilder.authGraph(
    navController: NavController,
    modifier: Modifier = Modifier,
    config: AuthConfig = AuthConfig(),
    onAuthSuccess: (email: String) -> Unit
) {
    composable(AuthNavDestinationsKey.LOGIN) {
        val loginViewModel = hiltViewModel<LoginWGCViewModel>()
        LaunchedEffect(Unit) {
            loginViewModel.navigationEvent.collect { event ->
                when (event) {
                    is AuthNavDestinations.LoginScreen.ForgotPassword -> navController.navigate(
                        AuthNavDestinationsKey.FORGOT_PASSWORD
                    )
                    is AuthNavDestinations.LoginScreen.RegisterUser -> navController.navigate(
                        AuthNavDestinationsKey.REGISTER_USER
                    )
                    is AuthNavDestinations.LoginScreen.LoginSuccess -> onAuthSuccess(event.email)
                }
            }
        }
        LoginWGCScreen(modifier = modifier)
    }

    composable(AuthNavDestinationsKey.FORGOT_PASSWORD) {
        val forgotPasswordViewModel = hiltViewModel<ForgotPasswordViewModel>()
        LaunchedEffect(Unit) {
            forgotPasswordViewModel.navigationEvent.collect { event ->
                when (event) {
                    AuthNavDestinations.ForgotPasswordScreen.Login -> navController.popBackStack()
                }
            }
        }
        ForgotPasswordScreen(modifier = modifier)
    }

    composable(AuthNavDestinationsKey.REGISTER_USER) {
        val registerUserViewModel = hiltViewModel<RegisterUserViewModel>()
        LaunchedEffect(Unit) {
            registerUserViewModel.navigationEvent.collect { event ->
                when (event) {
                    AuthNavDestinations.RegisterUserScreen.RegisterAddress -> {
                        if (config.enableAddressRegistration) {
                            navController.navigate(AuthNavDestinationsKey.REGISTER_ADDRESS)
                        } else if (config.enableCarRegistration) {
                            navController.navigate(AuthNavDestinationsKey.REGISTER_CAR)
                        } else {
                            onAuthSuccess(registerUserViewModel.uiState.value.email)
                        }
                    }
                    AuthNavDestinations.RegisterUserScreen.RegisterCar -> {
                        if (config.enableCarRegistration) {
                            navController.navigate(AuthNavDestinationsKey.REGISTER_CAR)
                        } else {
                            onAuthSuccess(registerUserViewModel.uiState.value.email)
                        }
                    }
                    AuthNavDestinations.RegisterUserScreen.Login -> navController.popBackStack()
                }
            }
        }
        RegisterUserScreen(modifier = modifier)
    }

    if (config.enableAddressRegistration) {
        composable(AuthNavDestinationsKey.REGISTER_ADDRESS) {
            val registerAddressViewModel = hiltViewModel<RegisterAddressViewModel>()
            LaunchedEffect(Unit) {
                registerAddressViewModel.navigationEvent.collect { event ->
                    when (event) {
                        AuthNavDestinations.RegisterAddress.RegisterUser -> {
                            if (config.enableCarRegistration) {
                                navController.navigate(AuthNavDestinationsKey.REGISTER_CAR)
                            } else {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
            RegisterAddressScreen(modifier = modifier)
        }
    }

    if (config.enableCarRegistration) {
        composable(AuthNavDestinationsKey.REGISTER_CAR) {
            val registerCarViewModel = hiltViewModel<RegisterCarViewModel>()
            LaunchedEffect(Unit) {
                registerCarViewModel.navigationEvent.collect { event ->
                    when (event) {
                        AuthNavDestinations.RegisterCar.RegisterAddress -> navController.popBackStack()
                        AuthNavDestinations.RegisterCar.RegisterUser -> {
                            navController.popBackStack()
                            navController.popBackStack()
                        }
                    }
                }
            }
            RegisterCarScreen(modifier = modifier)
        }
    }
}
