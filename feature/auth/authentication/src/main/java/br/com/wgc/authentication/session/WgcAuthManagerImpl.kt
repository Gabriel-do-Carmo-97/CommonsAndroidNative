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
 * ImplementaÃ§Ã£o Singleton do gerenciador de sessÃ£o corporativa [WgcAuthManager].
 *
 * MantÃ©m o estado da sessÃ£o do usuÃ¡rio sincronizado entre o backend e a persistÃªncia local.
 *
 * @param authRepository RepositÃ³rio de autenticaÃ§Ã£o do OmniBackend.
 * @param dataStore Gerenciador de persistÃªncia de credenciais locais.
 */
@Singleton
class WgcAuthManagerImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStore: DataStorePreferencesCore
) : WgcAuthManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

    override val currentUser: OmniUser?
        get() = when (val state = sessionState.value) {
            is AuthSessionState.Authenticated -> state.user
            else -> null
        }

    override val isAuthenticated: Boolean
        get() = sessionState.value is AuthSessionState.Authenticated

    override suspend fun isUserLogged(): Boolean {
        return when (val result = authRepository.isUserLogged()) {
            is DataResult.Success -> result.data
            is DataResult.Failure -> false
        }
    }

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
 * MÃ³dulo Hilt que vincula a implementaÃ§Ã£o [WgcAuthManagerImpl] ao contrato [WgcAuthManager].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthSessionModule {
    /**
     * Associa o singleton de gerenciamento de sessÃ£o Ã  sua interface.
     *
     * @param impl InstÃ¢ncia concreta de [WgcAuthManagerImpl].
     * @return Contrato pÃºblico de gerenciamento de sessÃ£o [WgcAuthManager].
     */
    @Binds
    @Singleton
    abstract fun bindWgcAuthManager(impl: WgcAuthManagerImpl): WgcAuthManager
}