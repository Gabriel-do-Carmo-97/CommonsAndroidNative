package br.com.gds.promotions.screen

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
class PromotionsViewModelTest {
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
    fun `initial state contains promotions story configuration`() = runTest {
        val viewModel = PromotionsViewModel()
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Ofertas Relâmpago WGC", state.userName)
            assertEquals(4, state.segmentCount)
            assertEquals(0, state.activeSegmentIndex)
            assertFalse(state.isLiked)
        }
    }

    @Test
    fun `onToggleLike toggles liked status`() = runTest {
        val viewModel = PromotionsViewModel()

        viewModel.onToggleLike()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isLiked)
        }
    }

    @Test
    fun `onReplyChange updates reply text`() = runTest {
        val viewModel = PromotionsViewModel()

        viewModel.onReplyChange("Quero cupom")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Quero cupom", state.replyText)
        }
    }

    @Test
    fun `nextStory and previousStory traverse segment index`() = runTest {
        val viewModel = PromotionsViewModel()

        viewModel.nextStory()
        assertEquals(1, viewModel.uiState.value.activeSegmentIndex)

        viewModel.nextStory()
        assertEquals(2, viewModel.uiState.value.activeSegmentIndex)

        viewModel.previousStory()
        assertEquals(1, viewModel.uiState.value.activeSegmentIndex)
    }
}
