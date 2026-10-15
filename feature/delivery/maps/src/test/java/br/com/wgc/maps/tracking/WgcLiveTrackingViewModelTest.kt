package br.com.wgc.maps.tracking

import app.cash.turbine.test
import br.wgc.omnibackend.core.model.database.geo.LocationRequest
import br.wgc.omnibackend.core.repository.realtime.GeolocationRepository
import br.wgc.omnibackend.core.utils.DataResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Suíte de testes unitários para [WgcLiveTrackingViewModel].
 *
 * Valida a conexão reativa com [GeolocationRepository], atualização de coordenadas GPS
 * e execução de callbacks de contato com o motorista parceiro.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WgcLiveTrackingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val geolocationRepository = mockk<GeolocationRepository>()

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
        val viewModel = WgcLiveTrackingViewModel(geolocationRepository)

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
    fun `onContactDriverClick triggers registered contact callback`() = runTest {
        val viewModel = WgcLiveTrackingViewModel(geolocationRepository)
        var contactTriggered = false

        viewModel.initializeTracking(
            entityId = "driver_101",
            onContact = { contactTriggered = true }
        )

        viewModel.onContactDriverClick()
        assertTrue(contactTriggered)
    }
}
