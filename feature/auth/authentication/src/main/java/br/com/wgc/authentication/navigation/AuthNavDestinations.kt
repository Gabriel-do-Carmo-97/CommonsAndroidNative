package br.com.wgc.authentication.navigation

/**
 * Contrato selado que define todas as rotas e destinos de navegaÃ§Ã£o do fluxo de autenticaÃ§Ã£o.
 */
sealed interface AuthNavDestinations {
    /**
     * Destinos originados a partir da tela de login.
     */
    sealed class LoginScreen : AuthNavDestinations {
        /**
         * Sucesso na autenticaÃ§Ã£o contendo o e-mail validado.
         *
         * @property email EndereÃ§o de e-mail do usuÃ¡rio autenticado.
         */
        data class LoginSuccess(val email: String) : LoginScreen()

        /**
         * Redirecionamento para a recuperaÃ§Ã£o de senha.
         */
        data object ForgotPassword : LoginScreen()

        /**
         * Redirecionamento para cadastro de novo usuÃ¡rio.
         */
        data object RegisterUser : LoginScreen()
    }

    /**
     * Destinos originados a partir da tela de cadastro de usuÃ¡rio.
     */
    sealed class RegisterUserScreen : AuthNavDestinations {
        /**
         * AvanÃ§o para o cadastro de endereÃ§o.
         */
        data object RegisterAddress : RegisterUserScreen()

        /**
         * AvanÃ§o para cadastro de veÃ­culo (quando aplicÃ¡vel ao app parceiro).
         */
        data object RegisterCar : RegisterUserScreen()

        /**
         * Retorno para tela de login.
         */
        data object Login : RegisterUserScreen()
    }

    /**
     * Destinos originados a partir da tela de recuperaÃ§Ã£o de senha.
     */
    sealed class ForgotPasswordScreen : AuthNavDestinations {
        /**
         * Retorno para tela de login.
         */
        data object Login : ForgotPasswordScreen()
    }

    /**
     * Destinos originados a partir do cadastro de endereÃ§o.
     */
    sealed class RegisterAddress : AuthNavDestinations {
        /**
         * Retorno para a tela de dados do usuÃ¡rio.
         */
        data object RegisterUser : RegisterAddress()
    }

    /**
     * Destinos originados a partir do cadastro de veÃ­culos.
     */
    sealed class RegisterCar : AuthNavDestinations {
        /**
         * Retorno para tela de dados do usuÃ¡rio.
         */
        data object RegisterUser : RegisterCar()

        /**
         * TransiÃ§Ã£o para tela de endereÃ§o.
         */
        data object RegisterAddress : RegisterCar()
    }
}