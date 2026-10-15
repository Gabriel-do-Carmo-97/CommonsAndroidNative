package br.com.wgc.authentication.session

import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.wgc.omnibackend.core.model.OmniUser
import br.wgc.omnibackend.core.repository.AuthRepository
import br.wgc.omnibackend.core.utils.DataResult
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação corporativa Singleton do gerenciador de sessão [WgcAuthManager].
 *
 * Sincroniza em tempo real o fluxo de autenticação provido pelo [AuthRepository] do OmniBackend 1.2.0,
 * mantendo as preferências locais do DataStore alinhadas em caso de desconexão.
 *
 * ### 📌 Quando Usar:
 * Fornecido automaticamente via injeção de dependência Hilt em escopo de Singleton para
 * garantir uma única fonte da verdade sobre o estado de login em toda a aplicação.
 *
 * ### 🛠️ Modo de Usar:
 * Injetado automaticamente pelo Hilt onde for solicitado [WgcAuthManager]:
 * ```kotlin
 * @Inject
 * lateinit var authManager: WgcAuthManager
 * ```
 *
 * @param authRepository Repositório unificado de autenticação do OmniBackend.
 * @param dataStore Gerenciador de persistência segura do Core.
 */
@Singleton
class WgcAuthManagerImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStore: DataStorePreferencesCore
) : WgcAuthManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Estado observável da sessão mapeado a partir do stream de autenticação do backend.
     */
    override val sessionState: StateFlow<AuthSessionState> = authRepository.authState
        .map { user ->
            if (user != null) {
                AuthSessionState.Authenticated(user)
            } else {
                AuthSessionState.Unauthenticated
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = AuthSessionState.Loading
        )

    /**
     * Retorna o usuário logado atual, ou `null` caso deslogado.
     */
    override val currentUser: OmniUser?
        get() = when (val state = sessionState.value) {
            is AuthSessionState.Authenticated -> state.user
            else -> null
        }

    /**
     * Indica se a sessão atual possui um usuário válido autenticado.
     */
    override val isAuthenticated: Boolean
        get() = sessionState.value is AuthSessionState.Authenticated

    /**
     * Verifica de forma assíncrona se há um usuário ativo no backend.
     *
     * @return `true` caso autenticado com sucesso no backend, `false` em caso de erro ou deslogado.
     */
    override suspend fun isUserLogged(): Boolean {
        return when (val result = authRepository.isUserLogged()) {
            is DataResult.Success -> result.data
            is DataResult.Failure -> false
        }
    }

    /**
     * Executa o logout no servidor e limpa credenciais salvas no armazenamento local.
     *
     * @return [Result] indicando o término da operação de logout.
     */
    override suspend fun logout(): Result<Unit> {
        return when (val result = authRepository.signOut()) {
            is DataResult.Success -> {
                dataStore.removeKey("wgc_auth_saved_email")
                dataStore.saveBoolean("wgc_auth_remember_me", false)
                Result.success(Unit)
            }
            is DataResult.Failure -> {
                Result.failure(Exception("Falha ao deslogar"))
            }
        }
    }
}

/**
 * Módulo Hilt que vincula a implementação [WgcAuthManagerImpl] ao contrato público [WgcAuthManager].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthSessionModule {
    /**
     * Associa o singleton de gerenciamento de sessão à sua interface pública.
     *
     * @param impl Instância concreta de [WgcAuthManagerImpl].
     * @return Contrato público de gerenciamento de sessão [WgcAuthManager].
     */
    @Binds
    @Singleton
    abstract fun bindWgcAuthManager(impl: WgcAuthManagerImpl): WgcAuthManager
}