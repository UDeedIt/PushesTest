package pro.udeedit.devtools.pushestest.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.MainViewModel
import pro.udeedit.devtools.pushestest.ui.SettingsState
import pro.udeedit.devtools.pushestest.ui.components.AnimatedButtonSection
import pro.udeedit.devtools.pushestest.ui.dialogs.InfoDialog
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

/**
 * The stateful entry point for the Pushes Test application screen.
 *
 * This Composable connects the [MainViewModel] to the UI. It manages the visibility
 * of the [ModalBottomSheet] and the [InfoDialog] using local state, while delegating
 * business logic actions back to the ViewModel.
 *
 * @param viewModel The Hilt-injected ViewModel providing the app's [SettingsState].
 * @param onPermissionRequest Callback triggered when a notification action requires system permissions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushesTestScreen(
    viewModel: MainViewModel = viewModel(),
    onPermissionRequest: (List<String>) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.state
    val scope = rememberCoroutineScope()

    // Local UI states for overlays
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var infoData by remember { mutableStateOf<Pair<Int, Int>?>(null) } // (titleRes, bodyRes)

    // Bridge logic to connect UI events to ViewModel/System actions
    PushesTestScreen(
        state = uiState,
        onTitleChange = { viewModel.onTitleChange(it) },
        onBodyChange = { viewModel.onBodyChange(it) },
        onShuffleClick = { viewModel.shuffleMockData() },
        onSendClick = {
            viewModel.vibrateButtonClick()
            val needed = viewModel.getRequiredPermissions()
            if (needed.isEmpty()) {
                viewModel.onSendClick(context as android.app.Activity)
            } else {
                onPermissionRequest(needed)
            }
        },
        onStopClick = {
            viewModel.vibrateButtonClick()
            viewModel.stopPeriodicNotifications()
        },
        onSettingsClick = { showSheet = true }
    )

    // Modal Bottom Sheet management for Settings
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { if (showSheet) showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            SettingsBottomSheet(
                viewModel = viewModel,
                onInfoClick = { title, body -> infoData = title to body },
                onClose = {
                    scope.launch { sheetState.hide() }
                    if (showSheet) showSheet = false
                }
            )
        }
    }

    // Custom Info Dialog management
    // Placed outside the BottomSheet to ensure correct window layering
    infoData?.let { (titleRes, bodyRes) ->
        InfoDialog(
            title = stringResource(titleRes),
            message = stringResource(bodyRes),
            onDismiss = { if (infoData != null) infoData = null }
        )
    }
}

/**
 * A stateless wrapper for [PushesTestContent].
 *
 * This overload serves as a bridge for the Stateful screen and is also
 * utilized in Previews to simulate different UI states without ViewModel dependencies.
 */
@Composable
fun PushesTestScreen(
    state: SettingsState,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onShuffleClick: () -> Unit,
    onSendClick: () -> Unit,
    onStopClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    PushesTestContent(
        state = state,
        onTitleChange = onTitleChange,
        onBodyChange = onBodyChange,
        onShuffleClick = onShuffleClick,
        onSendClick = onSendClick,
        onStopClick = onStopClick,
        onSettingsClick = onSettingsClick
    )
}

/**
 * The core UI layout implementation for Pushes Test.
 *
 * Features:
 * - Responsive layout: Uses [widthIn] to center content on tablets while remaining full-width on phones.
 * - Vertical bias: Employs weighted spacers to position the input block at the optimal 25/75 vertical ratio.
 * - Branded Styling: Integrated Material 3 [TopAppBar] and customized [OutlinedTextField] colors.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushesTestContent(
    state: SettingsState,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onShuffleClick: () -> Unit,
    onSendClick: () -> Unit,
    onStopClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.app_name),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                painterResource(R.drawable.outline_notification_settings_24),
                                stringResource(R.string.settings),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP SPACER (Bias: 0.25)
            Spacer(modifier = Modifier.weight(0.25f))

            Column(
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.main_inputs_spacing))
                    .widthIn(max = dimensionResource(R.dimen.main_container_width_in)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Notification Title Input
                OutlinedTextField(
                    value = state.notificationTitle,
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.hint_notification_title)) },
                    readOnly = state.isMockEnabled,
                    trailingIcon = {
                        if (state.isMockEnabled) {
                            IconButton(onClick = onShuffleClick) {
                                Icon(
                                    painterResource(R.drawable.rounded_settings_backup_restore_24),
                                    null
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.main_inputs_spacing)))

                // Notification Body Input
                OutlinedTextField(
                    value = state.notificationBody,
                    onValueChange = onBodyChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.hint_notification_body)) },
                    readOnly = state.isMockEnabled,
                    minLines = if (state.isMultiline || state.useBigText) 6 else 2,
                    maxLines = if (state.isMultiline || state.useBigText) 6 else 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.main_inputs_spacing_2)))

                // Animated Button Section (Send/Stop Toggle)
                AnimatedButtonSection(
                    isActive = state.isPeriodicActive,
                    onSend = onSendClick,
                    onStop = onStopClick
                )
            }

            // BOTTOM SPACER (Weight: 0.75)
            Spacer(modifier = Modifier.weight(0.75f))
        }
    }
}

// --- Previews ---

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PushesTestContentPreview() {
    PushesTestTheme {
        PushesTestContent(
            state = SettingsState(notificationTitle = "Preview Title", isMockEnabled = true),
            onTitleChange = {},
            onBodyChange = {},
            onShuffleClick = {},
            onSendClick = {},
            onStopClick = {},
            onSettingsClick = {}
        )
    }
}
