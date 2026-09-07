package br.com.gds.loyalty.screen

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
class LoyaltyViewModelTest {

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
    fun `initial state has silver tier and 850 points`() = runTest {
        val viewModel = LoyaltyViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(850, state.currentPoints)
            assertEquals(LoyaltyTier.SILVER, state.tier)
            assertEquals(7, state.currentStamps)
        }
    }

    @Test
    fun `addStamp increments stamps and points`() = runTest {
        val viewModel = LoyaltyViewModel()

        viewModel.addStamp()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(8, state.currentStamps)
            assertEquals(900, state.currentPoints)
        }
    }

    @Test
    fun `redeeming a reward deducts points and marks reward redeemed`() = runTest {
        val viewModel = LoyaltyViewModel()

        viewModel.redeemReward("r1") // requires 300 pts

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(550, state.currentPoints)
            assertTrue(state.rewards.first { it.id == "r1" }.isRedeemed)
        }
    }
}
