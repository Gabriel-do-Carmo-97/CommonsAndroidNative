package br.com.gds.authentication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import br.com.gds.authentication.config.AuthConfig

/**
 * Componente Composable de alto nível para renderização completa e independente
 * do fluxo de autenticação com NavHost próprio.
 *
 * @param modifier Modificador de layout do Composable.
 * @param config Configurações opcionais de telas e funcionalidades ([AuthConfig]).
 * @param authSuccess Callback disparado quando a autenticação for concluída com sucesso.
 */
@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier,
    config: AuthConfig = AuthConfig(),
    authSuccess: (email: String) -> Unit = {}
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AuthNavDestinationsKey.LOGIN
    ) {
        authGraph(
            navController = navController,
            modifier = modifier,
            config = config,
            onAuthSuccess = authSuccess
        )
    }
}