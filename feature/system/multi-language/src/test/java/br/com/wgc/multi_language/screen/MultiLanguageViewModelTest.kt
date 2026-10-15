package br.com.wgc.multi_language.screen

import app.cash.turbine.test
import br.com.wgc.core.sharedPreferences.InMemoryKeyValueStorage
import br.com.wgc.multi_language.locale.AppLanguage
import br.com.wgc.multi_language.locale.DynamicLocaleManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MultiLanguageViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var storage: InMemoryKeyValueStorage
    private lateinit var localeManager: DynamicLocaleManager
    private lateinit var viewModel: MultiLanguageViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        storage = InMemoryKeyValueStorage()
        localeManager = DynamicLocaleManager(storage)
        viewModel = MultiLanguageViewModel(localeManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state contains title and default PORTUGUESE language`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Módulo de Internacionalização WGC", state.title)
            assertEquals(AppLanguage.PORTUGUESE, state.currentLanguage)
            assertEquals(3, state.availableLanguages.size)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `selectLanguage updates state and localeManager to ENGLISH`() = runTest {
        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(AppLanguage.PORTUGUESE, initial.currentLanguage)

            viewModel.selectLanguage(AppLanguage.ENGLISH)

            val updated = awaitItem()
            assertEquals(AppLanguage.ENGLISH, updated.currentLanguage)
            assertEquals(AppLanguage.ENGLISH, localeManager.currentLanguage.value)
        }
    }
}
