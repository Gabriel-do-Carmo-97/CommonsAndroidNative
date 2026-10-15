package br.com.wgc.settings.screen

import app.cash.turbine.test
import br.com.wgc.authentication.session.AuthSessionState
import br.com.wgc.authentication.session.WgcAuthManager
import br.wgc.omnibackend.core.model.OmniUser
import br.com.wgc.core.security.EncryptedSharedPreferencesCore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Suíte de testes unitários para [SettingsViewModel].
 *
 * Valida o carregamento reativo de dados da conta e persistência de preferências
 * em armazenamento criptografado ([EncryptedSharedPreferencesCore]).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val authManager = mockk<WgcAuthManager>(relaxed = true)
    private val encryptedStorage = mockk<EncryptedSharedPreferencesCore>(relaxed = true)
    private val sessionFlow = MutableStateFlow<AuthSessionState>(AuthSessionState.Unauthenticated)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { authManager.sessionState } returns sessionFlow
        every { encryptedStorage.getBoolean(SettingsViewModel.PREF_NOTIFICATIONS_KEY, true) } returns true
        every { encryptedStorage.getBoolean(SettingsViewModel.PREF_DARK_MODE_KEY, false) } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state observes authenticated user`() = runTest {
        val testUser = OmniUser(uid = "456", email = "maria@wgc.com.br", displayName = "Maria WGC")
        every { authManager.currentUser } returns testUser
        sessionFlow.value = AuthSessionState.Authenticated(testUser)

        val viewModel = SettingsViewModel(authManager, encryptedStorage)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Maria WGC", state.userName)
            assertEquals("maria@wgc.com.br", state.email)
            assertTrue(state.notificationsEnabled)
            assertFalse(state.darkModeEnabled)
        }
    }

    @Test
    fun `toggle settings updates uiState flags and saves in encrypted storage`() = runTest {
        val viewModel = SettingsViewModel(authManager, encryptedStorage)

        viewModel.onToggleNotifications(false)
        viewModel.onToggleDarkMode(true)

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.notificationsEnabled)
            assertTrue(state.darkModeEnabled)
        }

        verify { encryptedStorage.saveBoolean(SettingsViewModel.PREF_NOTIFICATIONS_KEY, false) }
        verify { encryptedStorage.saveBoolean(SettingsViewModel.PREF_DARK_MODE_KEY, true) }
    }

    @Test
    fun `onLogoutClick calls authManager logout`() = runTest {
        coEvery { authManager.logout() } returns Result.success(Unit)
        val viewModel = SettingsViewModel(authManager, encryptedStorage)

        viewModel.onLogoutClick()
        advanceUntilIdle()

        coVerify(exactly = 1) { authManager.logout() }
    }
}
