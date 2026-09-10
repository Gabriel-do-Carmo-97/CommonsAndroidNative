package br.com.wgc.order_tracking.screen

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderTrackingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state starts at preparing status with positive ETA`() = runTest {
        val viewModel = OrderTrackingViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(OrderStatus.PREPARING, state.status)
            assertEquals(25, state.estimatedMinutesRemaining)
            assertEquals("#WGC-89421", state.orderId)
        }
    }

    @Test
    fun `advanceStatus moves order from preparing to out for delivery then delivered`() = runTest {
        val viewModel = OrderTrackingViewModel()

        viewModel.advanceStatus()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(OrderStatus.OUT_FOR_DELIVERY, state.status)
            assertEquals(10, state.estimatedMinutesRemaining)
        }

        viewModel.advanceStatus()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(OrderStatus.DELIVERED, state.status)
            assertEquals(0, state.estimatedMinutesRemaining)
        }
    }
}
