package br.com.gds.emergency.screen

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EmergencyViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(testDispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun `initial state contains title`() = runTest {
        val viewModel = EmergencyViewModel()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Módulo de Botão de Pânico WGC", state.title)
        }
    }
}
