package br.com.gds.authentication.navigation

sealed interface AuthNavDestinations {
    sealed class LoginScreen : AuthNavDestinations {
        data class LoginSuccess(val email: String) : LoginScreen()
        object ForgotPassword : LoginScreen()
        object RegisterUser : LoginScreen()
    }
    sealed class RegisterUserScreen : AuthNavDestinations {
        object RegisterAddress : RegisterUserScreen()
        object RegisterCar : RegisterUserScreen()
    }
    sealed class ForgotPasswordScreen : AuthNavDestinations {
        object Login : ForgotPasswordScreen()
    }
    sealed class RegisterAddress : AuthNavDestinations {
        object RegisterUser : RegisterAddress()
    }
    sealed class RegisterCar : AuthNavDestinations {
        object RegisterUser : RegisterCar()
        object RegisterAddress : RegisterCar()
    }
}