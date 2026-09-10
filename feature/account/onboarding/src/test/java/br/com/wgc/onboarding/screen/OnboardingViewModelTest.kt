package br.com.wgc.onboarding.screen

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

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
    fun `initial state starts at first page and not completed`() = runTest {
        val viewModel = OnboardingViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(0, state.currentPageIndex)
            assertFalse(state.isCompleted)
            assertEquals(3, state.pages.size)
        }
    }

    @Test
    fun `nextPage navigates forward through pages and completes at end`() = runTest {
        val viewModel = OnboardingViewModel()

        viewModel.nextPage()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.currentPageIndex)
            assertFalse(state.isCompleted)
        }

        viewModel.nextPage()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.currentPageIndex)
            assertFalse(state.isCompleted)
        }

        viewModel.nextPage()
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isCompleted)
        }
    }

    @Test
    fun `completeOnboarding sets isCompleted immediately`() = runTest {
        val viewModel = OnboardingViewModel()

        viewModel.completeOnboarding()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isCompleted)
        }
    }
}
