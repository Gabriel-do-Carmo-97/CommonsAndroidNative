package br.com.gds.media_picker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.gds.media_picker.screen.MediaPickerScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaPickerScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun mediaPickerScreen_rendersTitle() {
        composeTestRule.setContent { MediaPickerScreen() }
        composeTestRule.onNodeWithText("Módulo de Captura de Mídia e QR Code").assertIsDisplayed()
    }
}
