package pro.udeedit.devtools.pushestest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PtPrimaryDark,
    onPrimary = PtOnPrimaryDark,
    primaryContainer = PtPrimaryContainerDark,
    onPrimaryContainer = PtOnPrimaryContainerDark,
    surface = PtSurfaceDark,
    onSurface = PtOnSurfaceDark,
    outline = PtOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PtPrimaryLight,
    onPrimary = PtOnPrimaryLight,
    primaryContainer = PtPrimaryContainerLight,
    onPrimaryContainer = PtOnPrimaryContainerLight,
    surface = PtSurfaceLight,
    onSurface = PtOnSurfaceLight,
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
