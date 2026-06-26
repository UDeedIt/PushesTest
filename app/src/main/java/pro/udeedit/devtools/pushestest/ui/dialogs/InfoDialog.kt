package pro.udeedit.devtools.pushestest.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

/**
 * A custom Material 3 informational dialog.
 *
 * This Composable serves as the window-level wrapper for the info system.
 * It configures [DialogProperties] to allow the internal card to handle
 * its own responsive width constraints.
 *
 * @param title The text to be displayed as the dialog header.
 * @param message The detailed information or hint text.
 * @param onDismiss Callback executed when the user requests to close the dialog.
 */
@Composable
fun InfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Encapsulated UI content to support clean rendering in Previews
        InfoDialogContent(title, message, onDismiss)
    }
}

/**
 * The visual implementation of the info dialog card.
 *
 * Features:
 * - Responsive sizing: Capped at the standard container width for tablets.
 * - Material 3 Styling: Utilizes custom corner radii and surface coloring.
 * - Advanced Typography: Implements justified text alignment for optimal readability
 *   of technical documentation.
 */
@Composable
fun InfoDialogContent(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.dialog_padding_horizontal))
            .widthIn(max = dimensionResource(R.dimen.main_container_width_in)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.dialog_corner_radius)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.dialog_inner_padding)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Branded Information Icon
            Icon(
                painter = painterResource(R.drawable.ic_info_24),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.dialog_icon_size)),
                tint = MaterialTheme.colorScheme.primary
            )

            // Dynamic Centered Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_large))
            )

            // Justified Message Body
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Justify,
                    lineHeight = 24.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 32.dp)
            )

            // Confirmation Action
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.End)
                    .height(dimensionResource(R.dimen.button_height))
                    .widthIn(min = dimensionResource(R.dimen.dialog_button_min_width)),
                shape = RoundedCornerShape(dimensionResource(R.dimen.dialog_button_corner_radius)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(R.string.lbl_close),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// PREVIEWS

/**
 * Renders a visual preview of the info dialog in both Light and Dark themes.
 * Utilizes [InfoDialogContent] directly to bypass window manager requirements
 * in the IDE preview panel.
 */
@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun InfoDialogPreview() {
    PushesTestTheme {
        InfoDialogContent(
            title = "Mock Data",
            message = "Generates diverse developer scenarios like GitHub PRs or Server Alerts. When ON, manual input is locked to prevent typing errors during testing.",
            onDismiss = {}
        )
    }
}
