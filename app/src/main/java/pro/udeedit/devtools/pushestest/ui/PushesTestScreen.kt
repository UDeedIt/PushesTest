package pro.udeedit.devtools.pushestest.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme
import pro.udeedit.devtools.pushestest.utils.AppSetting

// The "Entry Point" Composable (Used in Activity)
@Composable
fun PushesTestScreen(viewModel: MainViewModel = viewModel()) {
    // This is the version used in the Activity
    // It collects the state from the real ViewModel
    val uiState by viewModel.state

    PushesTestScreen(
        state = uiState,
        // Explicitly cast 'Any' to the specific types that ViewModel expects
        onAction = { setting, value ->
            when (value) {
                is Boolean -> viewModel.set(setting, value)
                is Int -> viewModel.set(setting, value)
            }
        },
        onTitleChange = { viewModel.onTitleChange(it) },
        onBodyChange = { viewModel.onBodyChange(it) },
        onShuffleClick = { viewModel.shuffleMockData() },
        onPeriodicToggle = { viewModel.togglePeriodic(it) }
    )
}

@Composable
fun PushesTestScreen(
    state: SettingsState,
    onAction: (AppSetting, Any) -> Unit,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onShuffleClick: () -> Unit,
    onPeriodicToggle: (Boolean) -> Unit
) {
    // This is the "Master Container" used by both the Activity and Preview
    PushesTestContent(
        state = state,
        onTitleChange = onTitleChange,
        onBodyChange = onBodyChange,
        onShuffleClick = onShuffleClick,
        onSendClick = { onPeriodicToggle(true) },
        onStopClick = { onPeriodicToggle(false) },
        onSettingsClick = { /* We will add this next */ }
    )
}


// The "Stateless" UI (Used in Previews)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushesTestContent(
    state: SettingsState, // Simple data class
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
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(painterResource(R.drawable.outline_notification_settings_24), "Settings")
                        }
                    }
                )
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier.padding(16.dp).widthIn(max = 500.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TITLE
                OutlinedTextField(
                    value = state.notificationTitle, // Access via state
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.hint_notification_title)) },
                    readOnly = state.isMockEnabled,
                    trailingIcon = {
                        if (state.isMockEnabled) {
                            IconButton(onClick = onShuffleClick) {
                                Icon(painterResource(R.drawable.rounded_settings_backup_restore_24), null)
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // BODY Input
                OutlinedTextField(
                    value = state.notificationBody,
                    onValueChange = onBodyChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.hint_notification_body)) },
                    readOnly = state.isMockEnabled,
                    minLines = if (state.isMultiline) 6 else 2,
                    maxLines = if (state.isMultiline) 6 else 2
                )


                Spacer(modifier = Modifier.height(24.dp))

                // BUTTONS (With your vertical swag animation)
//                AnimatedButtonSection(
//                    isActive = state.isPeriodicActive,
//                    onSend = onSendClick,
//                    onStop = onStopClick
//                )

                // Wrap the buttons in AnimatedContent
                AnimatedContent(
                    targetState = state.isPeriodicActive,
                    transitionSpec = {
                        val springSpec = spring<Float>(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )

                        // Force the incoming content to have a higher Z-index
                        val zIndex = if (targetState) 1f else 0f

                        (slideInVertically { it } + fadeIn() + scaleIn(initialScale = 0.8f))
                            .togetherWith(slideOutVertically { -it } + fadeOut())
                            .also { it.targetContentZIndex = zIndex } // THE FIX: Explicit Z-Index

//                        (slideInVertically(animationSpec = spring(stiffness = Spring.StiffnessLow)) { it } +
//                                fadeIn() +
//                                scaleIn(initialScale = 0.8f, animationSpec = springSpec))
//                            .togetherWith(slideOutVertically { -it } + fadeOut())
//                            // This ensures the layers don't look messy during rapid clicks
//                            .using(SizeTransform(clip = false))
                    },
                    // Dynamic layering
                    modifier = Modifier.zIndex(if (state.isPeriodicActive) 1f else 0f),
                    label = "ButtonSwapAnimation"

                ) { active ->

                    if (active) {
                        // THE STOP BUTTON
                        Button(
                            onClick = onStopClick,
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(6.dp) // Matching your XML corner radius
                        ) {
                            Icon(painterResource(R.drawable.ic_autostop_24), contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.lbl_stop_sending))
                        }

                    } else {
                        // THE SEND BUTTON
                        Button(
                            onClick = onSendClick,
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(painterResource(R.drawable.ic_send_24), contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.lbl_send_notification))
                        }
                    }
                }
            }
        }
    }
}


// PREVIEW

// Preview uses simple data, no ViewModel needed!
@Preview(showBackground = true)
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