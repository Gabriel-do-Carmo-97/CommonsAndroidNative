package br.com.wgc.authentication.navigation

/**
 * Chaves constantes de rotas para o grafo de navegação de autenticação.
 */
data object AuthNavDestinationsKey {
    /** Rota da tela de login. */
    const val LOGIN = "login"

    /** Rota de cadastro de usuário. */
    const val REGISTER_USER = "register_user"

    /** Rota de cadastro de endereço. */
    const val REGISTER_ADDRESS = "register_address"

    /** Rota de cadastro de veículo parceiro. */
    const val REGISTER_CAR = "register_car"

    /** Rota de recuperação de senha. */
    const val FORGOT_PASSWORD = "forgot_password"
}