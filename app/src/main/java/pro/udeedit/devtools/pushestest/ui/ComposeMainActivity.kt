package pro.udeedit.devtools.pushestest.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import pro.udeedit.devtools.anarchist.AnarchistPermissionStatus
import pro.udeedit.devtools.anarchist.AnarchistPermissionUtils
import pro.udeedit.devtools.pushestest.ui.screens.PushesTestScreen
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme
import pro.udeedit.devtools.pushestest.utils.REQUEST_PERMISSION_CODE

/**
 * Main entry point for the Pushes Test application.
 *
 * This Activity serves as the primary host for the Jetpack Compose UI. It manages
 * the intersection between the Android OS and the application logic, specifically:
 * - Window management for Full-Screen Intent (FSI) support.
 * - Runtime permission management via the internal "anarchist" library.
 *
 * Features:
 * - Managed via Hilt Dependency Injection.
 * - Handles complex Window Manager flags to support Full-Screen Intents
 *    on locked devices across multiple Android versions (API 24 - 36).
 * - MVI State observation via [MainViewModel].
 */
@AndroidEntryPoint
class ComposeMainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Configures Activity and Window flags to bypass keyguards and turn the screen on.
         * Supports both modern O_MR1+ APIs and legacy WindowManager flags for
         * backward compatibility with older devices.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        enableEdgeToEdge()

        setContent {
            // Use the activity-scoped viewModel to ensure the permission result
            // bridges to the same instance
            PushesTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    PushesTestScreen(
                        viewModel = viewModel,
                        // Pass the launcher trigger down to the UI
                        onPermissionRequest = {
                            handleNotificationPermission()
                        }
                    )
                }
            }
        }
    }

    /**
     * Bridges the system permission callback to the "anarchist" library.
     * If permission is granted, it signals the ViewModel to proceed with the notification.
     */
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            processPermissionResult()
        }
    }

    /**
     * Internal logic to check and request the POST_NOTIFICATIONS permission.
     * Uses the "anarchist" library to trigger the system dialog or proceed if already allowed.
     */
    private fun handleNotificationPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(Manifest.permission.POST_NOTIFICATIONS)
        } else mutableListOf()

        val result = AnarchistPermissionUtils.checkAndRequestPermissions(
            this,
            permissions,
            REQUEST_PERMISSION_CODE
        )

        if (result.finalStatus == AnarchistPermissionStatus.ALLOWED) {
            viewModel.startNotificationProcess(this)
        }
    }

    /**
     * Evaluates the permission status after user interaction.
     * Redirects to the notification logic on success or settings on permanent denial.
     */
    private fun processPermissionResult() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(Manifest.permission.POST_NOTIFICATIONS)
        } else mutableListOf()

        val result = AnarchistPermissionUtils.checkAndRequestPermissions(
            this,
            permissions,
            REQUEST_PERMISSION_CODE,
            checkStatusOnly = true
        )

        when (result.finalStatus) {
            AnarchistPermissionStatus.ALLOWED -> viewModel.startNotificationProcess(this)
            AnarchistPermissionStatus.DENIED_PERMANENTLY -> {
                AnarchistPermissionUtils.askUserToRequestPermissionExplicitly(this)
            }
            else -> {}
        }
    }

}


// PREVIEW

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PushesTestPreview() {
    PushesTestTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            PushesTestScreen(
                state = SettingsState(
                    notificationTitle = "Pushes Test Preview",
                    isMockEnabled = true,
                    // You can toggle this to test the "Stop" UI in preview
                    isPeriodicActive = false
                ),
                onTitleChange = {},
                onBodyChange = {},
                onShuffleClick = {},
                onSendClick = {},
                onStopClick = {},
                onSettingsClick = {}
            )
        }
    }
}
