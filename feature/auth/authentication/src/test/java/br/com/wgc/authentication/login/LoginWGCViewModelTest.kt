package br.com.wgc.authentication.login

import app.cash.turbine.test
import br.com.wgc.authentication.navigation.AuthNavDestinations
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.core.security.biometric.BiometricAuthHelper
import br.wgc.omnibackend.core.model.OmniUser
import br.wgc.omnibackend.core.repository.AuthRepository
import br.wgc.omnibackend.core.utils.AppError
import br.wgc.omnibackend.core.utils.DataResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

import io.mockk.every
import kotlinx.coroutines.flow.flowOf

/**
 * Testes unitários para [LoginWGCViewModel].
 *
 * Valida o ciclo completo de autenticação desacoplada através do [AuthRepository],
 * testando emissão de eventos reativos e tratamento de erros.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginWGCViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authRepository = mockk<AuthRepository>()
    private val dataStore = mockk<DataStorePreferencesCore>(relaxed = true)
    private val biometricAuthHelper = mockk<BiometricAuthHelper>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { dataStore.getBooleanFlow(any(), any()) } returns flowOf(false)
        every { dataStore.getStringFlow(any(), any()) } returns flowOf("")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when onLoginClick is triggered and login succeeds, navigationEvent should emit LoginSuccess`() = runTest {
        val email = "user@wgc.com"
        val password = "password123"
        val mockUser = mockk<OmniUser>(relaxed = true)

        coEvery { authRepository.login(email, password) } returns DataResult.Success(mockUser)

        val viewModel = LoginWGCViewModel(authRepository, dataStore, biometricAuthHelper)
        viewModel.setCredentials(email, password)

        viewModel.navigationEvent.test {
            viewModel.onLoginClick()
            val event = awaitItem()
            assertTrue(event is AuthNavDestinations.LoginScreen.LoginSuccess)
        }
    }

    @Test
    fun `when onLoginClick fails, navigationEvent should not emit success`() = runTest {
        val email = "user@wgc.com"
        val password = "wrong_password"

        coEvery { authRepository.login(email, password) } returns DataResult.Failure(AppError.Auth.InvalidCredentials)

        val viewModel = LoginWGCViewModel(authRepository, dataStore, biometricAuthHelper)
        viewModel.setCredentials(email, password)

        viewModel.navigationEvent.test {
            viewModel.onLoginClick()
            expectNoEvents()
        }
    }
}
