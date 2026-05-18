package uz.tubeme.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkScheme = darkColorScheme(
    primary = NeonBlue,
    onPrimary = Color.Black,
    secondary = NeonPurple,
    onSecondary = Color.White,
    tertiary = NeonPink,
    background = Bg,
    onBackground = TextPri,
    surface = Surface,
    onSurface = TextPri,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextSec,
    error = Danger,
    outline = BorderStrong
)

private val LightScheme = lightColorScheme(
    primary = NeonBlue,
    secondary = NeonPurple,
    tertiary = NeonPink,
    background = Color(0xFFF6F7FB),
    surface = Color.White,
    onBackground = Color(0xFF0A0A0A),
    onSurface = Color(0xFF0A0A0A)
)

@Composable
fun TubeMeTheme(useDark: Boolean = true, content: @Composable () -> Unit) {
    val colors = if (useDark) DarkScheme else LightScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDark
        }
    }
    MaterialTheme(
        colorScheme = colors,
        typography = TubeMeTypography,
        content = content
    )
}
