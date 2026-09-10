package br.com.wgc.authentication.navigation

sealed interface AuthNavDestinations {
    sealed class LoginScreen : AuthNavDestinations {
        data class LoginSuccess(val email: String) : LoginScreen()
        data object ForgotPassword : LoginScreen()
        data object RegisterUser : LoginScreen()
    }
    sealed class RegisterUserScreen : AuthNavDestinations {
        data object RegisterAddress : RegisterUserScreen()
        data object RegisterCar : RegisterUserScreen()
        data object Login : RegisterUserScreen()
    }
    sealed class ForgotPasswordScreen : AuthNavDestinations {
        data object Login : ForgotPasswordScreen()
    }
    sealed class RegisterAddress : AuthNavDestinations {
        data object RegisterUser : RegisterAddress()
    }
    sealed class RegisterCar : AuthNavDestinations {
        data object RegisterUser : RegisterCar()
        data object RegisterAddress : RegisterCar()
    }
}