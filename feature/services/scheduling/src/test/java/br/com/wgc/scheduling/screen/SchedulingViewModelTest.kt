package br.com.wgc.scheduling.screen

import app.cash.turbine.test
import br.wgc.omnibackend.core.repository.FirestoreRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SchedulingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val firestoreRepository = mockk<FirestoreRepository>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has default service, date and time slots`() = runTest {
        val viewModel = SchedulingViewModel(firestoreRepository)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Corte de Cabelo & Barba", state.selectedService.name)
            assertEquals("Hoje", state.selectedDate)
            assertEquals("10:30", state.selectedTime)
        }
    }

    @Test
    fun `selecting service, date and time updates uiState`() = runTest {
        val viewModel = SchedulingViewModel(firestoreRepository)

        val targetService = viewModel.uiState.value.services[1]
        viewModel.selectService(targetService)
        viewModel.selectDate("Amanhã")
        viewModel.selectTime("14:00")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(targetService.id, state.selectedService.id)
            assertEquals("Amanhã", state.selectedDate)
            assertEquals("14:00", state.selectedTime)
        }
    }

    @Test
    fun `confirmBooking updates state to isBookingConfirmed`() = runTest {
        val viewModel = SchedulingViewModel(firestoreRepository)

        viewModel.confirmBooking()

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.isSubmitting)

            testScheduler.runCurrent()

            val confirmedState = awaitItem()
            assertTrue(confirmedState.isBookingConfirmed)
        }
    }
}
