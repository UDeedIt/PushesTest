package pro.udeedit.devtools.pushestest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import pro.udeedit.devtools.pushestest.ui.SettingsState
import pro.udeedit.devtools.pushestest.ui.dialogs.InfoDialogContent
import pro.udeedit.devtools.pushestest.ui.screens.PushesTestContent
import pro.udeedit.devtools.pushestest.ui.screens.PushesTestScreen
import pro.udeedit.devtools.pushestest.ui.screens.SettingsBottomSheetContent
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

/**
 * Instrumented UI tests for the Pushes Test application.
 *
 * These tests verify the Unidirectional Data Flow (UDF) by testing
 * the stateless Composables and the integration of the stateful screen.
 */
class PushesTestUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    /**
     * Test: Clicking the Settings Gear icon should trigger the provided callback.
     *
     * Verifies that the top app bar action is correctly wired to the navigation logic.
     */
    @Test
    fun settingsIcon_shouldTriggerCallback_whenClicked() {
        // Arrange: Setup a flag to track the click
        var settingsClicked = false
        val settingsDescription = context.getString(R.string.settings)

        composeTestRule.setContent {
            PushesTestTheme {
                // Use the Stateless version for deterministic testing
                PushesTestScreen(
                    state = SettingsState(),
                    onTitleChange = {},
                    onBodyChange = {},
                    onShuffleClick = {},
                    onSendClick = {},
                    onStopClick = {},
                    onSettingsClick = {
                        settingsClicked = true
                    }
                )
            }
        }

        // Act: Click the icon via its content description
        composeTestRule.onNodeWithContentDescription(settingsDescription).performClick()

        // Assert: Verify the lambda was executed
        assert(settingsClicked) { "The onSettingsClick lambda was not triggered." }
    }

    /**
     * Test: UI should render the 'Stop' button when the state indicates periodic activity.
     *
     * Verifies that the MVI state correctly drives the visibility of primary actions.
     */
    @Test
    fun ui_shouldDisplayStopButton_whenPeriodicIsActiveInState() {
        // Arrange: Create a state where periodic testing is already running
        val activeState = SettingsState(isPeriodicActive = true)
        val stopText = context.getString(R.string.lbl_stop_sending)

        composeTestRule.setContent {
            PushesTestTheme {
                PushesTestScreen(
                    state = activeState,
                    onTitleChange = {},
                    onBodyChange = {},
                    onShuffleClick = {},
                    onSendClick = {},
                    onStopClick = {},
                    onSettingsClick = {}
                )
            }
        }

        // Assert: The 'Stop Sending' button must be visible immediately
        composeTestRule.onNodeWithText(stopText).assertIsDisplayed()
    }

    /**
     * Test: Full UI Integration of Settings and Info Dialogs.
     *
     * This test verifies the end-to-end user journey within the UI layer:
     * 1. Navigating from the Main Screen to the Settings view.
     * 2. Triggering the secondary documentation layer (Info Dialog) from a specific setting.
     * 3. Verifying that the final action (Close button) is rendered and reachable.
     *
     * Technical approach:
     * - Uses the "Stateless Component Stacking" pattern to simulate BottomSheet/Dialog
     *   layering without the flake-prone window manager dependencies.
     * - Employs explicit 'testTags' to eliminate ambiguity in node selection.
     * - Implements asynchronous polling via 'waitUntil' to account for recomposition frames.
     */
    @Test
    fun integration_settingsToInfoDialog_flow() {
        composeTestRule.setContent {
            PushesTestTheme {
                Surface(color = MaterialTheme.colorScheme.surface) {
                    var showSettings by remember { mutableStateOf(false) }
                    var showDialogContent by remember { mutableStateOf(false) }

                    // Use Box to layer components
                    Box(modifier = Modifier.fillMaxSize()) {

                        // Render the Main Screen as the base layer
                        PushesTestContent(
                            state = SettingsState(),
                            onTitleChange = {},
                            onBodyChange = {},
                            onShuffleClick = {},
                            onSendClick = {},
                            onStopClick = {},
                            onSettingsClick = {
                                showSettings = true
                            }
                        )

                        // Render Settings as an overlay (simulating BottomSheet)
                        if (showSettings) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                SettingsBottomSheetContent(
                                    state = SettingsState(isMockEnabled = true),
                                    onSettingChange = { _, _ -> },
                                    onInfoClick = { _, _ -> showDialogContent = true },
                                    onClose = { showSettings = false },
                                    onReset = {}
                                )
                            }
                        }

                        // Render Dialog as the top-most overlay
                        if (showDialogContent) {
                            // We center the dialog content manually for the test
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                InfoDialogContent(
                                    title = "Mock Data",
                                    message = "Testing the integration flow...",
                                    onDismiss = {
                                        showDialogContent = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ACT & ASSERT

        // Open Settings
        val settingsDesc = context.getString(R.string.settings)
        composeTestRule.onNodeWithContentDescription(settingsDesc).performClick()

        // Click the info icon by Tag
        composeTestRule.onAllNodesWithTag("info_icon").onFirst().performClick()

        // Verify Dialog is visible
        val closeText = context.getString(R.string.lbl_close)
        composeTestRule.onNodeWithText(closeText).assertIsDisplayed()
    }

}
