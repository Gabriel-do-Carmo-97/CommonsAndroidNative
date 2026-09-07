package br.com.gds.feedback.screen

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
class FeedbackViewModelTest {

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
    fun `initial state contains default NPS and rating values`() = runTest {
        val viewModel = FeedbackViewModel(firestoreRepository)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(10, state.npsScore)
            assertEquals(5, state.starRating)
            assertEquals("Atendimento", state.selectedCategory)
            assertTrue(state.comment.isEmpty())
        }
    }

    @Test
    fun `updating ratings and comments updates uiState`() = runTest {
        val viewModel = FeedbackViewModel(firestoreRepository)

        viewModel.onNpsScoreChanged(8)
        viewModel.onStarRatingChanged(4)
        viewModel.onCategorySelected("Entrega")
        viewModel.onCommentChanged("Entrega super rápida!")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(8, state.npsScore)
            assertEquals(4, state.starRating)
            assertEquals("Entrega", state.selectedCategory)
            assertEquals("Entrega super rápida!", state.comment)
        }
    }

    @Test
    fun `submitFeedback triggers submission and updates isSubmitted`() = runTest {
        val viewModel = FeedbackViewModel(firestoreRepository)

        viewModel.submitFeedback()

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState.isSubmitting)

            testScheduler.runCurrent()

            val completedState = awaitItem()
            assertTrue(completedState.isSubmitted)
        }
    }
}
