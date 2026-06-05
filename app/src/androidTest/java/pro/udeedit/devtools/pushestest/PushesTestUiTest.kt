package pro.udeedit.devtools.pushestest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import pro.udeedit.devtools.pushestest.ui.PushesTestScreen
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

class PushesTestUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun sendButton_swapsToStopButton_whenClicked() {
        // Start the app in the test
        composeTestRule.setContent {
            PushesTestTheme {
                PushesTestScreen()
            }
        }

        // Find the Send button and click it
        composeTestRule.onNodeWithText("Send Notification").performClick()

        // Assert that the Stop button is now visible
        composeTestRule.onNodeWithText("Stop").assertIsDisplayed()
    }
}
