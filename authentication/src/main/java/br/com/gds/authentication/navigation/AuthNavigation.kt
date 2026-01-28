package br.com.gds.authentication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import br.com.gds.authentication.forgotPassword.ForgotPasswordScreen
import br.com.gds.authentication.forgotPassword.ForgotPasswordViewModel
import br.com.gds.authentication.login.LoginWGCScreen
import br.com.gds.authentication.login.LoginWGCViewModel
import br.com.gds.authentication.registerAddress.RegisterAddressScreen
import br.com.gds.authentication.registerAddress.RegisterAddressViewModel
import br.com.gds.authentication.registerCar.RegisterCarScreen
import br.com.gds.authentication.registerCar.RegisterCarViewModel
import br.com.gds.authentication.registerUser.RegisterUserScreen
import br.com.gds.authentication.registerUser.RegisterUserViewModel

@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier,
    authSuccess: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AuthNavDestinationsKey.LOGIN
    ) {
        composable(AuthNavDestinationsKey.LOGIN) {
            val loginViewModel = hiltViewModel<LoginWGCViewModel>()
            LaunchedEffect(Unit) {
                loginViewModel.navigationEvent.collect { event ->
                    when (event) {
                        is AuthNavDestinations.LoginScreen.ForgotPassword -> navController.navigate(
                            AuthNavDestinationsKey.FORGOT_PASSWORD
                        )
                        is AuthNavDestinations.LoginScreen.RegisterUser ->  navController.navigate(
                            AuthNavDestinationsKey.REGISTER_USER
                        )

                        is AuthNavDestinations.LoginScreen.LoginSuccess -> authSuccess()
                    }
                }
            }
            LoginWGCScreen(modifier = modifier)
        }
        composable(AuthNavDestinationsKey.FORGOT_PASSWORD) {
            val forgotPasswordViewModel = hiltViewModel<ForgotPasswordViewModel>()
            LaunchedEffect(Unit){
                forgotPasswordViewModel.navigationEvent.collect { event ->
                    when(event){
                        AuthNavDestinations.ForgotPasswordScreen.Login -> navController.popBackStack()
                    }
                }
            }
            ForgotPasswordScreen(modifier = modifier)
        }
        composable(AuthNavDestinationsKey.REGISTER_USER) {
            val registerUserViewModel = hiltViewModel<RegisterUserViewModel>()
            LaunchedEffect(Unit){
                registerUserViewModel.navigationEvent.collect { event ->
                    when(event){
                        AuthNavDestinations.RegisterUserScreen.RegisterAddress -> navController.navigate(
                            AuthNavDestinationsKey.REGISTER_ADDRESS
                        )
                        AuthNavDestinations.RegisterUserScreen.RegisterCar -> navController.navigate(
                            AuthNavDestinationsKey.REGISTER_CAR
                        )
                    }
                }
            }
            RegisterUserScreen(modifier = modifier)
        }
        composable(AuthNavDestinationsKey.REGISTER_ADDRESS) {
            val registerAddressViewModel = hiltViewModel<RegisterAddressViewModel>()
            LaunchedEffect(Unit){
                registerAddressViewModel.navigationEvent.collect { event ->
                    when (event) {
                        AuthNavDestinations.RegisterAddress.RegisterUser -> navController.popBackStack()
                    }
                }
            }
            RegisterAddressScreen(modifier = modifier)
        }
        composable(AuthNavDestinationsKey.REGISTER_CAR) {
            val registerCarViewModel = hiltViewModel<RegisterCarViewModel>()
            LaunchedEffect(Unit){
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