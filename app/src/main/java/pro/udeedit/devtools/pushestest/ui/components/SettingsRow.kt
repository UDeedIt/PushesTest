package pro.udeedit.devtools.pushestest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme
import androidx.compose.material3.ripple
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource

/**
 * A specialized settings list item containing a label and a toggle switch.
 *
 * Design Features:
 * - Superscript Info Icon: An information icon is anchored to the label text like
 *   a footnote, providing access to educational documentation.
 * - Touch Target Optimization: The info icon is wrapped in a larger, transparent [Box]
 *   to ensure it meets accessibility standards without increasing its visual footprint.
 * - Automated Testing: Includes [testTag] attributes to support deterministic
 *   Android Instrumented UI tests.
 *
 * @param label The localized text describing the setting.
 * @param checked The current state of the switch (ON/OFF).
 * @param onCheckedChange Callback triggered when the switch is toggled.
 * @param onInfoClick Callback triggered when the superscript information icon is tapped.
 */
@Composable
fun SettingsRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.settings_row_height)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label and Info Icon section
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // The Box wrapper allows us to define a larger clickable area (40dp)
            // than the actual visual icon (14dp), solving tap-accuracy issues.
            Box(
                modifier = Modifier
                    .testTag("info_icon") // Used by PushesTestUiTest to locate the node
                    .offset(
                        x = (-(dimensionResource(R.dimen.superscript_offset_x))),
                        y = (-(dimensionResource(R.dimen.superscript_offset_y)))
                    )
                    .size(dimensionResource(R.dimen.info_icon_layout_size))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(
                            bounded = false,
                            radius = dimensionResource(R.dimen.superscript_ripple_radius)
                        ),
                        onClick = onInfoClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info_24),
                    contentDescription = stringResource(R.string.info),
                    modifier = Modifier
                        .testTag("info_icon")
                        .size(dimensionResource(R.dimen.info_icon_visual_size)),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Standard Material 3 Switch for preference toggling
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

// PREVIEWS

/**
 * Visualizes the SettingsRow in both Light and Dark modes.
 * Ensures the 'superscript' positioning remains visually balanced across themes.
 */
@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SettingsRowPreview() {
    PushesTestTheme {
        Surface {
            Column(modifier = Modifier.padding(dimensionResource(R.dimen.margin_horizontal_middle))) {
                SettingsRow(
                    label = "Use mock data",
                    checked = true,
                    onCheckedChange = {},
                    onInfoClick = {}
                )

                SettingsRow(
                    label = "Overwrite notifications",
                    checked = false,
                    onCheckedChange = {},
                    onInfoClick = {}
                )
            }
        }
    }
}
