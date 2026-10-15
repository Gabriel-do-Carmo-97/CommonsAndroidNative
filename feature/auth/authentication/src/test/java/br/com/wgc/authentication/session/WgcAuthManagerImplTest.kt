package br.com.wgc.authentication.session

import app.cash.turbine.test
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.core.security.EncryptedSharedPreferencesCore
import br.wgc.omnibackend.core.model.OmniUser
import br.wgc.omnibackend.core.repository.AuthRepository
import br.wgc.omnibackend.core.utils.DataResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Suíte de testes unitários corporativa para [WgcAuthManagerImpl].
 *
 * Valida a sincronização de sessão reativa, persistência de credenciais em armazenamento
 * criptografado ([EncryptedSharedPreferencesCore]), e o processo de logout seguro.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WgcAuthManagerImplTest {

    private val authRepository = mockk<AuthRepository>()
    private val dataStore = mockk<DataStorePreferencesCore>(relaxed = true)
    private val encryptedStorage = mockk<EncryptedSharedPreferencesCore>(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val dispatchers = object : CoroutineDispatchers {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    private val authStateFlow = MutableStateFlow<OmniUser?>(null)

    @Before
    fun setUp() {
        every { authRepository.authState } returns authStateFlow
        coEvery { authRepository.isUserLogged() } returns DataResult.Success(true)
        coEvery { authRepository.signOut() } returns DataResult.Success(Unit)
    }

    @Test
    fun `sessionState updates to Authenticated and saves encrypted token when user signs in`() = runTest {
        val user = OmniUser(uid = "usr_101", email = "joao@wgc.com.br", displayName = "João")
        val manager = WgcAuthManagerImpl(authRepository, dataStore, encryptedStorage, dispatchers)

        authStateFlow.value = user

        manager.sessionState.test {
            val state = awaitItem()
            assertTrue(state is AuthSessionState.Authenticated)
            assertEquals("usr_101", (state as AuthSessionState.Authenticated).user.uid)
            assertEquals(user, manager.currentUser)
            assertTrue(manager.isAuthenticated)
        }

        verify { encryptedStorage.saveString("wgc_encrypted_session_token", "usr_101") }
    }

    @Test
    fun `sessionState updates to Unauthenticated and clears encrypted token when user signs out`() = runTest {
        val manager = WgcAuthManagerImpl(authRepository, dataStore, encryptedStorage, dispatchers)

        authStateFlow.value = null

        manager.sessionState.test {
            val state = awaitItem()
            assertTrue(state is AuthSessionState.Unauthenticated)
            assertEquals(null, manager.currentUser)
            assertFalse(manager.isAuthenticated)
        }

        verify { encryptedStorage.remove("wgc_encrypted_session_token") }
    }

    @Test
    fun `logout signs out from repository and clears sensitive storage`() = runTest {
        val manager = WgcAuthManagerImpl(authRepository, dataStore, encryptedStorage, dispatchers)

        val result = manager.logout()

        assertTrue(result.isSuccess)
        coVerify { authRepository.signOut() }
        verify { encryptedStorage.remove("wgc_encrypted_session_token") }
        coVerify { dataStore.removeKey("wgc_auth_saved_email") }
        coVerify { dataStore.saveBoolean("wgc_auth_remember_me", false) }
    }

    @Test
    fun `isUserLogged returns true when repository reports logged user`() = runTest {
        val manager = WgcAuthManagerImpl(authRepository, dataStore, encryptedStorage, dispatchers)

        val isLogged = manager.isUserLogged()
        assertTrue(isLogged)
    }
}
