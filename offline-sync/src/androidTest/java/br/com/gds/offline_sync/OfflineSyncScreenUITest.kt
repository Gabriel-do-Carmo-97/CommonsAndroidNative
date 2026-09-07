package br.com.gds.offline_sync

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.offline_sync.screen.OfflineSyncScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineSyncScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun offlineSyncScreen_rendersTitle() {
        composeTestRule.setContent { OfflineSyncScreen() }
        composeTestRule.onNodeWithText("Módulo de Sincronização Offline WGC").assertIsDisplayed()
    }
}
