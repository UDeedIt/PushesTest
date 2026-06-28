package pro.udeedit.devtools.pushestest.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.em
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme

/**
 * A specialized structural header used to categorize settings groups.
 *
 * Features:
 * - Automatically transforms text to uppercase for a consistent sectional look.
 * - Utilizes branded primary colors and standardized vertical margins from resources.
 *
 * @param text The localized label for the category (e.g., "BEHAVIOR", "TIMING").
 * @param modifier Standard Compose modifier for external layout adjustments.
 */
@Composable
fun CategoryHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.titleMedium.copy(
            letterSpacing = 0.05.em
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(R.dimen.margin_vertical_middle),
                bottom = dimensionResource(R.dimen.margin_category_bottom)
            )
    )
}

// --- PREVIEWS ---

/**
 * Visualizes the category header in both Light and Dark themes to ensure
 * primary color contrast and letter spacing are rendered correctly.
 */
@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun CategoryHeaderPreview() {
    PushesTestTheme {
        CategoryHeader(text = "Behavior")
    }
}
