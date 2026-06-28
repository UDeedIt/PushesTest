package pro.udeedit.devtools.pushestest.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

/**
 * A highly interactive button container that animates transitions between
 * 'Send' and 'Stop' states.
 *
 * Features:
 * - State-driven [AnimatedContent] for seamless switching.
 * - Custom "Vertical Swag" transition: Incoming content slides up while outgoing content
 *   slides out vertically with a fade.
 * - Reactive Click Animation: Utilizes a spring-loaded scale effect to provide tactile
 *   visual feedback on every interaction.
 *
 * @param isActive Determines if the system is currently in periodic/active mode (Stop state).
 * @param onSend Callback for the primary notification trigger.
 * @param onStop Callback to terminate active notification loops.
 */
@Composable
fun AnimatedButtonSection(
    isActive: Boolean,
    onSend: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine the vertical slide distance in pixels based on current screen density
    val density = androidx.compose.ui.platform.LocalDensity.current
    val slideDistance = with(density) {
        dimensionResource(R.dimen.animation_slide_distance).roundToPx()
    }

    val scope = rememberCoroutineScope()
    // Utilizes the specialized helper for scale state
    val scale = rememberClickScale()

    AnimatedContent(
        targetState = isActive,
        contentKey = { active -> active },
        transitionSpec = {
            // New state springs up from the bottom, current state slides up and out
            (slideInVertically { slideDistance } + fadeIn())
                .togetherWith(slideOutVertically { -slideDistance } + fadeOut())
                .also {
                    // Force higher Z-index on the target state to prevent clipping artifacts
                    it.targetContentZIndex = if (targetState) 1f else 0f
                }
        },
        modifier = modifier.fillMaxWidth(),
        label = "ButtonSwap"
    ) { active ->

        if (active) {
            // THE STOP BUTTON: High visibility error style for loop termination
            Button(
                onClick = {
                    scope.launch { animateClick(scale) }
                    onStop()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.button_height))
                    .graphicsLayer(scaleX = scale.value, scaleY = scale.value),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(dimensionResource(R.dimen.btn_corner_radius))
            ) {
                Icon(
                    painterResource(R.drawable.ic_autostop_24),
                    null
                )

                Spacer(Modifier.width(dimensionResource(R.dimen.margin_horizontal_normal)))

                Text(
                    stringResource(R.string.lbl_stop_sending),
                    style = MaterialTheme.typography.titleMedium
                )
            }

        } else {
            // THE SEND BUTTON: Primary action style for notification publishing
            Button(
                onClick = {
                    scope.launch { animateClick(scale) }
                    onSend()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.button_height))
                    .graphicsLayer(scaleX = scale.value, scaleY = scale.value),
                shape = RoundedCornerShape(dimensionResource(R.dimen.btn_corner_radius))
            ) {
                Icon(
                    painterResource(R.drawable.ic_send_24),
                    null
                )

                Spacer(Modifier.width(dimensionResource(R.dimen.margin_horizontal_normal)))

                Text(
                    stringResource(R.string.lbl_send_notification),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * Creates and remembers an [Animatable] scale value for click feedback.
 */
@Composable
private fun rememberClickScale(): Animatable<Float, AnimationVector1D> {
    return remember { Animatable(1f) }
}

/**
 * Performs a spring-loaded "Pulse" animation sequence.
 *
 * 1. Rapidly scales down to simulate a physical press.
 * 2. Utilizes [Spring.DampingRatioMediumBouncy] to overshoot and settle
 *    back to the original size.
 */
private suspend fun animateClick(scale: Animatable<Float, AnimationVector1D>) {
    // Shrink (The "Press" feeling)
    scale.animateTo(0.92f, animationSpec = tween(100))
    // Spring back (The "Overshoot" feeling)
    scale.animateTo(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
}


// --- PREVIEWS ---

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AnimatedButtonSectionPreview() {
    PushesTestTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))) {
                AnimatedButtonSection(
                    isActive = false,
                    onSend = {},
                    onStop = {}
                )
                Spacer(Modifier.height(16.dp))
                AnimatedButtonSection(
                    isActive = true,
                    onSend = {},
                    onStop = {}
                )
            }
        }
    }
}
