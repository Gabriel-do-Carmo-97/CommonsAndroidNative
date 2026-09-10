package br.com.wgc.force_update.screen

import app.cash.turbine.test
import br.wgc.omnibackend.core.repository.RemoteConfigRepository
import br.wgc.omnibackend.core.utils.DataResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
class ForceUpdateViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val remoteConfigRepository = mockk<RemoteConfigRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `checkForUpdate detects mandatory update when min version is higher`() = runTest {
        coEvery { remoteConfigRepository.fetchAndActivate() } returns DataResult.Success(true)
        every { remoteConfigRepository.getLong("min_required_app_version") } returns DataResult.Success(5L)
        every { remoteConfigRepository.getBoolean("force_update_enabled") } returns DataResult.Success(true)
        every { remoteConfigRepository.getString("store_url") } returns DataResult.Success("https://play.google.com/store/apps/details?id=com.wgc.app")

        val viewModel = ForceUpdateViewModel(remoteConfigRepository)
        viewModel.checkForUpdate(currentVersionCode = 2L)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isUpdateRequired)
            assertTrue(state.isForceUpdate)
            assertEquals(5L, state.minRequiredVersionCode)
            assertEquals("https://play.google.com/store/apps/details?id=com.wgc.app", state.storeUrl)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `checkForUpdate detects app is up to date when current version is equal or higher`() = runTest {
        coEvery { remoteConfigRepository.fetchAndActivate() } returns DataResult.Success(true)
        every { remoteConfigRepository.getLong("min_required_app_version") } returns DataResult.Success(3L)
        every { remoteConfigRepository.getBoolean("force_update_enabled") } returns DataResult.Success(false)
        every { remoteConfigRepository.getString("store_url") } returns DataResult.Success("")

        val viewModel = ForceUpdateViewModel(remoteConfigRepository)
        viewModel.checkForUpdate(currentVersionCode = 4L)
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isUpdateRequired)
            assertFalse(state.isForceUpdate)
            assertFalse(state.isLoading)
        }
    }
}
