package pro.udeedit.devtools.pushestest.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
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

// The "Entry Point" Composable (Used in Activity)
@Composable
fun PushesTestScreen(viewModel: MainViewModel = viewModel()) {
    // Collect states from ViewModel
    val title by viewModel.notificationTitle
    val body by viewModel.notificationBody
    val isMocked by viewModel.isMockEnabled
    val isPeriodicActive by viewModel.isPeriodicActive

    // Pass those values to a "Stateless" version
    PushesTestContent(
        title = title,
        body = body,
        isMocked = isMocked,
        isPeriodicActive = isPeriodicActive,
        onTitleChange = { viewModel.onTitleChange(it) },
        onBodyChange = { viewModel.onBodyChange(it) },
        onShuffleClick = { viewModel.shuffleMockData() },
        onStopClick = { viewModel.togglePeriodic(false) },
        onSendClick = { viewModel.togglePeriodic(true) }
    )
}


// The "Stateless" UI (Used in Previews)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushesTestContent(
    title: String,
    body: String,
    isMocked: Boolean,
    isPeriodicActive: Boolean,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onShuffleClick: () -> Unit,
    onStopClick: () -> Unit,
    onSendClick: () -> Unit
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

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
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

                // Wrap the buttons in AnimatedContent
                AnimatedContent(
                    targetState = isPeriodicActive,
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
                    modifier = Modifier.zIndex(if (isPeriodicActive) 1f else 0f),
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

// Preview uses simple data, no ViewModel needed!
@Preview(showBackground = true)
@Composable
fun PushesTestPreview() {
    PushesTestTheme {
        PushesTestContent(
            title = "Mock Title",
            body = "Mock Notification Body Content",
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
