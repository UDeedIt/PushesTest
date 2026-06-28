package pro.udeedit.devtools.pushestest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//private val DarkColorScheme = darkColorScheme(
//    primary = PtPrimaryDark,
//    onPrimary = PtOnPrimaryDark,
//    primaryContainer = PtPrimaryContainerDark,
//    onPrimaryContainer = PtOnPrimaryContainerDark,
//    surface = PtSurfaceDark,
//    onSurface = PtOnSurfaceDark,
//    outline = PtOutlineDark
//)

private val DarkColorScheme = darkColorScheme(
    primary = PtPrimaryDark,
    onPrimary = PtOnPrimaryDark,
    surface = PtSurfaceDark, // Deep Grey/Blue background
    onSurface = PtOnSurfaceDark, // Off-white for Titles
    onSurfaceVariant = PtOnSurfaceVariantDark, // SOFTER grey for secondary text
    outline = PtOutlineDark, // For dividers and borders
    background = PtSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = PtPrimaryLight,
    onPrimary = PtOnPrimaryLight,
    primaryContainer = PtPrimaryContainerLight,
    onPrimaryContainer = PtOnPrimaryContainerLight,
    surface = PtSurfaceLight,
    background = Color.White,
    // THE FIX: Provide a soft grey/blue for the toolbar background
    surfaceVariant = Color(0xFFF0F4F8),
    onSurface = PtOnSurfaceLight,
    onSurfaceVariant = PtOutlineLight,
    outline = PtOutlineLight
)


@Composable
fun PushesTestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
