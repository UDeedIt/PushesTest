package pro.udeedit.devtools.pushestest.ui.components

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PtSettingsDropdown(
    label: String,
    options: Array<String>,
    selectedPosition: Int,
    onSelectionChange: (Int) -> Unit,
    onInfoClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.dropdown_vertical_padding))) {
        // Label with Superscript Info Icon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

            IconButton(
                onClick = onInfoClick,
                modifier = Modifier.offset(y = -dimensionResource(R.dimen.superscript_offset_y)).size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info_24),
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.info_icon_visual_size)),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // The Dropdown Menu
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = options.getOrElse(selectedPosition) { "" },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEachIndexed { index, selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption, style = MaterialTheme.typography.bodyLarge) },
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
