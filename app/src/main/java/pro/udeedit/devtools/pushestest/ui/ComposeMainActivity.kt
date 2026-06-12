package pro.udeedit.devtools.pushestest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

class ComposeMainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = viewModel()
            PushesTestTheme {
                PushesTestScreen(viewModel)
            }
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
@Preview(name = "Light Mode", showBackground = true)
@Composable
fun PushesTestPreview() {
    PushesTestTheme {
        PushesTestScreen(
            state = SettingsState(
                notificationTitle = "Pushes Test Preview",
                isMockEnabled = true
            ),
            onAction = { _, _ -> },
            onTitleChange = {},
            onBodyChange = {},
            onShuffleClick = {},
            onPeriodicToggle = {}
        )
    }
}