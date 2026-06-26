package pro.udeedit.devtools.pushestest.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

/**
 * A persistent header component for the Settings interface.
 *
 * This Composable provides primary navigation and utility actions:
 * - A Reset action to restore all application settings to their defaults.
 * - A centered Title to establish context within the [ModalBottomSheet].
 * - A Close action to dismiss the settings overlay.
 *
 * Features:
 * - Symmetrical Layout: Uses [Arrangement.SpaceBetween] to balance primary icons.
 * - Branded Icons: Utilizes larger [R.dimen.settings_icons_size] assets tinted with the
 *   theme's primary color for a high-end utility feel.
 *
 * @param onReset Callback triggered when the 'Restore Defaults' icon is tapped.
 * @param onClose Callback triggered when the 'Close' icon is tapped.
 */
@Composable
fun SettingsHeader(onReset: () -> Unit, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_normal)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // RESTORE DEFAULTS ACTION
        IconButton(onClick = onReset) {
            Icon(
                painter = painterResource(R.drawable.rounded_settings_backup_restore_24),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.settings_icons_size)),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // SECTION TITLE
        Text(
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        // DISMISS ACTION
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(R.drawable.rounded_close_24),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.settings_icons_size)),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// PREVIEWS

/**
 * Visualizes the SettingsHeader in both Light and Dark modes.
 * Ensures the primary color scheme provides sufficient contrast against the surface.
 */
@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SettingsHeaderPreview() {
    PushesTestTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            SettingsHeader(
                onReset = {},
                onClose = {}
            )
        }
    }
}
