package br.com.wgc.feedback

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.wgc.feedback.screen.FeedbackScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FeedbackScreenUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test fun feedbackScreen_rendersTitle() {
        composeTestRule.setContent { FeedbackScreen() }
        composeTestRule.onNodeWithText("Módulo de Feedback, NPS e In-App Review").assertIsDisplayed()
    }
}
