package br.com.wgc.authentication.login

import app.cash.turbine.test
import br.com.wgc.authentication.navigation.AuthNavDestinations
import br.com.wgc.core.dataStorePreferences.DataStorePreferencesCore
import br.com.wgc.core.security.biometric.BiometricAuthHelper
import br.wgc.omnibackend.core.utils.AppError
import br.wgc.omnibackend.firebase.domain.usecase.LoginUseCase
import br.wgc.omnibackend.firebase.utils.UseCaseResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
    private val useCase = mockk<LoginUseCase>()
    private val dataStore = mockk<DataStorePreferencesCore>(relaxed = true)
    private val biometricAuthHelper = mockk<BiometricAuthHelper>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when onLoginClick is triggered and login succeeds, navigationEvent should emit LoginSuccess`() = runTest {
        val email = "user@wgc.com"
        val password = "password123"

        coEvery { useCase(any(), any(), any()) } returns flowOf(UseCaseResult.Success("user123"))

        val viewModel = LoginWGCViewModel(useCase, dataStore, biometricAuthHelper)

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

        coEvery { useCase(any(), any(), any()) } returns flowOf(UseCaseResult.Failure(AppError.Auth.InvalidCredentials))

        val viewModel = LoginWGCViewModel(useCase, dataStore, biometricAuthHelper)

        viewModel.navigationEvent.test {
            viewModel.onLoginClick()
            expectNoEvents()
        }
    }
}
