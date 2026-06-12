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
import androidx.compose.ui.unit.dp
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme
import androidx.compose.material3.ripple
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import pro.udeedit.devtools.pushestest.utils.AppSetting

@Composable
fun SettingsRow(
    label: String,
    checked: Boolean,
//    onCheckedChange = {
//        viewModel.set(AppSetting.VIBRATION, it)
//    }, // Clean!
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
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically // Keeps text centered in row
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Wrap Icon in a Box to control Touch Target vs Visual Size
            Box(
                modifier = Modifier
                    .offset(
                        x = (-4).dp, // Pull it closer to the text
                        y = (-8).dp  // Push it up like a superscript
                    )
                    .size(40.dp) // Large touch target for the finger
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = false, radius = 20.dp),
                        onClick = onInfoClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info_24),
                    contentDescription = "Info",
                    modifier = Modifier.size(dimensionResource(R.dimen.info_icon_visual_size)),
                    tint = MaterialTheme.colorScheme.primary // Use Primary for that "D2D" look
                )
            }
        }

        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// PREVIEWS

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
private fun SettingsRowPreview() {
    PushesTestTheme {
//        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
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
//        }
    }
}