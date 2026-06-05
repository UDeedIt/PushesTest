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


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PushesTestTheme {
        // We use the "Stateless" version for the Preview to avoid
        // ViewModel initialization which fails because of CushyStorage
        PushesTestContent(
            title = "Preview Title",
            body = "Preview Body Content",
            isMocked = false,
            isPeriodicActive = true,
            onTitleChange = {},
            onBodyChange = {},
            onShuffleClick = {},
            onStopClick = {},
            onSendClick = {}
        )
    }
}