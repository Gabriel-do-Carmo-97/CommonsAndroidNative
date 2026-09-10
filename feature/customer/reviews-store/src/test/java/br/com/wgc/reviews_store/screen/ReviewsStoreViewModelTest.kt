package br.com.wgc.reviews_store.screen

import app.cash.turbine.test
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
class ReviewsStoreViewModelTest {

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
    fun `initial state has no votes and target none`() = runTest {
        val viewModel = ReviewsStoreViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(0, state.starRating)
            assertEquals(ReviewRedirectTarget.NONE, state.redirectTarget)
        }
    }

    @Test
    fun `5 stars rating routes to PLAY_STORE`() = runTest {
        val viewModel = ReviewsStoreViewModel()

        viewModel.selectRating(5)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(5, state.starRating)
            assertEquals(ReviewRedirectTarget.PLAY_STORE, state.redirectTarget)
            assertTrue(state.hasVoted)
        }
    }

    @Test
    fun `3 stars rating routes to INTERNAL_FEEDBACK`() = runTest {
        val viewModel = ReviewsStoreViewModel()

        viewModel.selectRating(3)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(3, state.starRating)
            assertEquals(ReviewRedirectTarget.INTERNAL_FEEDBACK, state.redirectTarget)
            assertTrue(state.hasVoted)
        }
    }

    @Test
    fun `completeReview sets isCompleted to true`() = runTest {
        val viewModel = ReviewsStoreViewModel()

        viewModel.selectRating(5)
        viewModel.completeReview()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isCompleted)
        }
    }
}
