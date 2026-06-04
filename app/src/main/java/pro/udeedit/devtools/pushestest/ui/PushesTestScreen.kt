package pro.udeedit.devtools.pushestest.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

// The "Entry Point" Composable (Used in Activity)
@Composable
fun PushesTestScreen(viewModel: MainViewModel = viewModel()) {
    // Collect states from ViewModel
    val title by viewModel.notificationTitle
    val body by viewModel.notificationBody
    val isMocked by viewModel.isMockEnabled

    // Pass those values to a "Stateless" version
    PushesTestContent(
        title = title,
        body = body,
        isMocked = isMocked,
        onTitleChange = { viewModel.onTitleChange(it) },
        onBodyChange = { viewModel.onBodyChange(it) },
        onShuffleClick = { viewModel.shuffleMockData() }
    )
}


// The "Stateless" UI (Used in Previews)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushesTestContent(
    title: String,
    body: String,
    isMocked: Boolean,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onShuffleClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { /* TODO: Open Settings */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_notification_settings_24),
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .widthIn(max = 500.dp), // Tablet Centering
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { onTitleChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.hint_notification_title)) },
                    readOnly = isMocked,
                    trailingIcon = {
                        if (isMocked) {
                            IconButton(onClick = { onShuffleClick() }) {
                                Icon(painterResource(R.drawable.rounded_settings_backup_restore_24), contentDescription = null)
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Body Input
                OutlinedTextField(
                    value = body,
                    onValueChange = { onBodyChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.hint_notification_body)) },
                    readOnly = isMocked,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(24.dp))

                // We will implement the Animated Buttons here next...
            }
        }
    }
}

// Preview uses simple data, no ViewModel needed!
@Preview(showBackground = true)
@Composable
fun PushesTestPreview() {
    PushesTestTheme {
        PushesTestContent(
            title = "Mock Title",
            body = "Mock Notification Body Content",
            isMocked = false,
            onTitleChange = {},
            onBodyChange = {},
            onShuffleClick = {}
        )
    }
}
