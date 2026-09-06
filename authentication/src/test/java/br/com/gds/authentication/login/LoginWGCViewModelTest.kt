package br.com.gds.authentication.login

import app.cash.turbine.test
import br.com.gds.authentication.navigation.AuthNavDestinations
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
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

@OptIn(ExperimentalCoroutinesApi::class)
class LoginWGCViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authRepository = mockk<AuthRepository>()
    private val dataStore = mockk<DataStorePreferencesCore>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when onLoginClick is triggered and auth succeeds, navigationEvent should emit LoginSuccess`() = runTest {
        val email = "user@wgc.com"
        val password = "password123"

        coEvery { authRepository.login(email, password) } returns DataResult.Success(
            OmniUser(id = "user123", email = email, displayName = "Test User")
        )

        val viewModel = LoginWGCViewModel(authRepository, dataStore)
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)

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

        coEvery { authRepository.login(email, password) } returns DataResult.Failure(
            AppError.Auth.InvalidCredentials
        )

        val viewModel = LoginWGCViewModel(authRepository, dataStore)
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)

        viewModel.navigationEvent.test {
            viewModel.onLoginClick()
            expectNoEvents()
        }
    }
}
