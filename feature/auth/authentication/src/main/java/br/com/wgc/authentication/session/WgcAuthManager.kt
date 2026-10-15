package br.com.wgc.authentication.session

import br.wgc.omnibackend.core.model.OmniUser
import kotlinx.coroutines.flow.StateFlow

/**
 * Representa os estados possíveis da sessão de autenticação global do usuário no app.
 *
 * ### 📌 Quando Usar:
 * Observe este estado em fluxos de navegação ou telas condicionais que alteram o conteúdo exibido
 * caso o usuário esteja logado ou deslogado.
 */
sealed interface AuthSessionState {
    /** Indica que a sessão está sendo verificada ou carregada do cache local / backend. */
    data object Loading : AuthSessionState

    /**
     * Sessão autenticada ativa.
     *
     * @property user Dados do usuário logado providos pelo OmniBackend.
     */
    data class Authenticated(val user: OmniUser) : AuthSessionState

    /** Usuário não logado ou sessão expirada. */
    data object Unauthenticated : AuthSessionState
}

/**
 * Fachada pública corporativa para gerenciamento da sessão do usuário.
 *
 * Permite que qualquer módulo de feature, bundle ou tela do aplicativo consumidor
 * verifique reativamente se há um usuário logado ou execute logout com invalidação de tokens.
 *
 * ### 📌 Quando Usar:
 * Injete [WgcAuthManager] em ViewModels, UseCases ou Interceptors sempre que uma operação
 * necessitar saber a identidade do usuário atual ou quando for disparada a ação de sair da conta.
 *
 * ### 🛠️ Modo de Usar:
 * ```kotlin
 * @HiltViewModel
 * class MeuViewModel @Inject constructor(
 *     private val authManager: WgcAuthManager
 * ) : ViewModel() {
 *
 *     val usuarioAtual = authManager.currentUser
 *
 *     fun deslogar() {
 *         viewModelScope.launch {
 *             authManager.logout()
 *         }
 *     }
 * }
 * ```
 */
interface WgcAuthManager {
    /**
     * Flow reativo contendo o estado da sessão em tempo real.
     */
    val sessionState: StateFlow<AuthSessionState>

    /**
     * Instância do [OmniUser] atualmente autenticado, ou `null` caso o usuário não esteja logado.
     */
    val currentUser: OmniUser?

    /**
     * Retorna `true` se existe uma sessão válida ativa no momento.
     */
    val isAuthenticated: Boolean

    /**
     * Realiza o logout do usuário no backend e limpa as credenciais salvas em disco.
     *
     * ### 📌 Quando Usar:
     * Acione este método ao finalizar a sessão do usuário ou após revogação de tokens no servidor.
     *
     * @return [Result] encapsulando sucesso ou falha na desconexão.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Checagem assíncrona se há um usuário ativo no cache/tokens.
     *
     * ### 📌 Quando Usar:
     * Útil na inicialização do app (Splash Screen) para decidir a rota inicial.
     *
     * @return `true` se o usuário está autenticado, `false` caso contrário.
     */
    suspend fun isUserLogged(): Boolean
}
