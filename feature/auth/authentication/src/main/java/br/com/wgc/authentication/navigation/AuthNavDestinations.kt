package br.com.wgc.authentication.navigation

/**
 * Contrato selado que define todas as rotas e destinos de navegação do fluxo de autenticação.
 */
sealed interface AuthNavDestinations {
    /**
     * Destinos originados a partir da tela de login.
     */
    sealed class LoginScreen : AuthNavDestinations {
        /**
         * Sucesso na autenticação contendo o e-mail validado.
         *
         * @property email Endereço de e-mail do usuário autenticado.
         */
        data class LoginSuccess(val email: String) : LoginScreen()

        /**
         * Redirecionamento para a recuperação de senha.
         */
        data object ForgotPassword : LoginScreen()

        /**
         * Redirecionamento para cadastro de novo usuário.
         */
        data object RegisterUser : LoginScreen()
    }

    /**
     * Destinos originados a partir da tela de cadastro de usuário.
     */
    sealed class RegisterUserScreen : AuthNavDestinations {
        /**
         * Avanço para o cadastro de endereço.
         */
        data object RegisterAddress : RegisterUserScreen()

        /**
         * Avanço para cadastro de veículo (quando aplicável ao app parceiro).
         */
        data object RegisterCar : RegisterUserScreen()

        /**
         * Retorno para tela de login.
         */
        data object Login : RegisterUserScreen()
    }

    /**
     * Destinos originados a partir da tela de recuperação de senha.
     */
    sealed class ForgotPasswordScreen : AuthNavDestinations {
        /**
         * Retorno para tela de login.
         */
        data object Login : ForgotPasswordScreen()
    }

    /**
     * Destinos originados a partir do cadastro de endereço.
     */
    sealed class RegisterAddress : AuthNavDestinations {
        /**
         * Retorno para a tela de dados do usuário.
         */
        data object RegisterUser : RegisterAddress()
    }

    /**
     * Destinos originados a partir do cadastro de veículos.
     */
    sealed class RegisterCar : AuthNavDestinations {
        /**
         * Retorno para tela de dados do usuário.
         */
        data object RegisterUser : RegisterCar()

        /**
         * Transição para tela de endereço.
         */
        data object RegisterAddress : RegisterCar()
    }
}