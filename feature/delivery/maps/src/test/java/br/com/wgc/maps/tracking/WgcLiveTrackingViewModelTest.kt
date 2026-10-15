package br.com.wgc.maps.tracking

import app.cash.turbine.test
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.wgc.omnibackend.core.model.database.geo.LocationRequest
import br.wgc.omnibackend.core.repository.realtime.GeolocationRepository
import br.wgc.omnibackend.core.utils.DataResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
 * Suíte de testes unitários para [WgcLiveTrackingViewModel].
 *
 * Valida a conexão reativa com [GeolocationRepository], atualização de coordenadas GPS,
 * filtragem estocástica com [MockLocationDetector] e execução de callbacks.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WgcLiveTrackingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val geolocationRepository = mockk<GeolocationRepository>()

    private val dispatchers = object : CoroutineDispatchers {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every {
            geolocationRepository.trackLocation(any(), any())
        } returns flowOf(
            DataResult.Success(
                LocationRequest(latitude = -23.55052, longitude = -46.633308, timestamp = 1700000000L)
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initializeTracking connects to geolocation flow and updates state with driver position`() = runTest {
        val viewModel = WgcLiveTrackingViewModel(geolocationRepository, dispatchers)

        viewModel.initializeTracking(
            entityType = "drivers",
            entityId = "driver_101",
            driverName = "Carlos Lima",
            destinationAddress = "Rua Oscar Freire, 100"
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Carlos Lima", state.driverName)
            assertEquals("Rua Oscar Freire, 100", state.destinationAddress)
            assertTrue(state.isTrackingActive)
            assertTrue(state.statusText.contains("-23.55052"))
        }
    }

    @Test
    fun `initializeTracking flags alert when mock location is detected`() = runTest {
        // Simulação de teletransporte (São Paulo para Nova York em 1 segundo)
        every {
            geolocationRepository.trackLocation(any(), any())
        } returns flowOf(
            DataResult.Success(
                LocationRequest(latitude = -23.55052, longitude = -46.633308, timestamp = 1000L)
            ),
            DataResult.Success(
                LocationRequest(latitude = 40.7128, longitude = -74.0060, timestamp = 2000L)
            )
        )

        val viewModel = WgcLiveTrackingViewModel(geolocationRepository, dispatchers)

        viewModel.initializeTracking(
            entityId = "driver_teleport",
            driverName = "Suspeito"
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isTrackingActive)
            assertTrue(state.statusText.contains("Localização simulada"))
        }
    }

    @Test
    fun `onContactDriverClick triggers registered contact callback`() = runTest {
        val viewModel = WgcLiveTrackingViewModel(geolocationRepository, dispatchers)
        var contactTriggered = false

        viewModel.initializeTracking(
            entityId = "driver_101",
            onContact = { contactTriggered = true }
        )

        viewModel.onContactDriverClick()
        assertTrue(contactTriggered)
    }
}
