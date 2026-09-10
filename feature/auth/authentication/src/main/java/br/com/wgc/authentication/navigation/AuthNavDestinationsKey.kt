package br.com.wgc.authentication.navigation

/**
 * Chaves constantes de rotas para o grafo de navegaÃ§Ã£o de autenticaÃ§Ã£o.
 */
data object AuthNavDestinationsKey {
    /** Rota da tela de login. */
    const val LOGIN = "login"

    /** Rota de cadastro de usuÃ¡rio. */
    const val REGISTER_USER = "register_user"

    /** Rota de cadastro de endereÃ§o. */
    const val REGISTER_ADDRESS = "register_address"

    /** Rota de cadastro de veÃ­culo parceiro. */
    const val REGISTER_CAR = "register_car"

    /** Rota de recuperaÃ§Ã£o de senha. */
    const val FORGOT_PASSWORD = "forgot_password"
}