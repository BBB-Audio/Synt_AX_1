package bbb.audio.syntAX1.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorSchemeGirlie = darkColorScheme(
    primary = Primary_Girlie,
    secondary = Secondary_Girlie,
    tertiary = Surface2_Girlie,
    background = Background_Girlie,
    surface = Surface1_Girlie,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

// As a fallback, the light theme will use the same dark colors.
private val LightColorSchemeGirlie = lightColorScheme(
    primary = Primary_Girlie,
    secondary = Secondary_Girlie,
    tertiary = Surface2_Girlie,
    background = Background_Girlie,
    surface = Surface1_Girlie,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun Synt_AX_1_GirlieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to prioritize the custom theme.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorSchemeGirlie
        else -> LightColorSchemeGirlie
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
