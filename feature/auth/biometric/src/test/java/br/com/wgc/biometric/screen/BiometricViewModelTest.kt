package br.com.wgc.biometric.screen

import androidx.fragment.app.FragmentActivity
import app.cash.turbine.test
import br.com.wgc.core.security.biometric.BiometricAuthHelper
import br.com.wgc.core.security.biometric.BiometricAuthResult
import br.com.wgc.core.security.biometric.BiometricAuthStatus
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BiometricViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val biometricAuthHelper = mockk<BiometricAuthHelper>()
    private val mockActivity = mockk<FragmentActivity>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state detects biometric ready when helper reports ready`() = runTest {
        every { biometricAuthHelper.canAuthenticate() } returns BiometricAuthStatus.Ready

        val viewModel = BiometricViewModel(biometricAuthHelper)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isBiometricReady)
            assertFalse(state.isAuthenticated)
        }
    }

    @Test
    fun `initial state detects biometric unavailable when helper reports not enrolled`() = runTest {
        every { biometricAuthHelper.canAuthenticate() } returns BiometricAuthStatus.NoneEnrolled

        val viewModel = BiometricViewModel(biometricAuthHelper)

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isBiometricReady)
            assertFalse(state.isAuthenticated)
        }
    }
}

