package pro.udeedit.devtools.pushestest.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

/**
 * A custom Material 3 Dropdown component designed to replicate the functionality
 * of a traditional Spinner with a specialized Pushes Test design.
 *
 * Features:
 * - A descriptive label with an integrated superscript information icon.
 * - [ExposedDropdownMenuBox] for a modern, read-only selection interface.
 * - Utilizes the theme's [MaterialTheme.colorScheme.surface] for dropdown menus
 *   to ensure Light/Dark mode compatibility.
 *
 * @param label The descriptive text displayed above the dropdown.
 * @param options An array of localized strings representing the selectable items.
 * @param selectedPosition The current index of the selected item in the [options] array.
 * @param onSelectionChange Callback triggered when a new item is selected.
 * @param onInfoClick Callback triggered when the superscript information icon is tapped.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PtSettingsDropdown(
    label: String,
    options: Array<String>,
    selectedPosition: Int,
    onSelectionChange: (Int) -> Unit,
    onInfoClick: () -> Unit
) {
    // Manages the visibility of the dropdown popup
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.vertical_padding_normal))) {

        // --- LABEL SECTION ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // The information icon is wrapped in a Box to provide a large touch target
            // while maintaining a small visual "superscript" size.
            Box(
                modifier = Modifier
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
                    modifier = Modifier.size(dimensionResource(R.dimen.info_icon_visual_size)),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // --- DROPDOWN SECTION ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            // Read-only field displaying the currently selected option
            OutlinedTextField(
                value = options.getOrElse(selectedPosition) { "" },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            // The actual menu containing the selectable list items
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEachIndexed { index, selectionOption ->
                    DropdownMenuItem(
                        text = {
                            Text(selectionOption, style = MaterialTheme.typography.bodyLarge)
                        },
                        onClick = {
                            onSelectionChange(index)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

// PREVIEWS

/**
 * Visualizes the PtSettingsDropdown in both Light and Dark modes.
 * Wraps the component in a themed [Surface] to ensure correct contrast for
 * both the outlined field and the dropdown text.
 */
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PtSettingsDropdownPreview() {
    PushesTestTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            PtSettingsDropdown(
                label = "Notification Importance",
                options = arrayOf("Urgent", "High", "Medium", "Low"),
                selectedPosition = 1,
                onSelectionChange = {},
                onInfoClick = {}
            )
        }
    }
}
