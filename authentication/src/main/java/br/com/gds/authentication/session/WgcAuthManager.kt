package br.com.gds.authentication.session

import br.wgc.omnibackend.core.model.OmniUser
import kotlinx.coroutines.flow.StateFlow

/**
 * Representa os estados possíveis da sessão de autenticação global do usuário no app.
 */
sealed interface AuthSessionState {
    data object Loading : AuthSessionState
    data class Authenticated(val user: OmniUser) : AuthSessionState
    data object Unauthenticated : AuthSessionState
}

/**
 * Fachada pública centralizada para gerenciamento da sessão do usuário.
 * Permite que qualquer módulo ou tela do aplicativo consumidor verifique se há um usuário logado
 * ou execute logout atômico com limpeza de credenciais locais.
 */
interface WgcAuthManager {
    /**
     * Flow reativo contendo o estado da sessão em tempo real.
     */
    val sessionState: StateFlow<AuthSessionState>

    /**
     * Instância do OmniUser atualmente autenticado, ou null caso deslogado.
     */
    val currentUser: OmniUser?

    /**
     * Retorna se existe uma sessão válida ativa no momento.
     */
    val isAuthenticated: Boolean

    /**
     * Realiza o logout do usuário no backend e limpa as credenciais salvas em disco.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Checagem assíncrona se há um usuário ativo no cache/tokens.
     */
    suspend fun isUserLogged(): Boolean
}
