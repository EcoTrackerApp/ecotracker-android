package fr.umontpellier.ecotracker.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

private val DarkColorScheme = darkColorScheme(
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    secondary = Color.Black.copy(alpha = 0.4f),
    background = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val typography = Typography(
    bodyLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = TextUnit(18f, TextUnitType.Sp),
        lineHeight = TextUnit(22f, TextUnitType.Sp),
        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp)
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = TextUnit(15f, TextUnitType.Sp),
        lineHeight = TextUnit(18f, TextUnitType.Sp),
        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp)
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = TextUnit(9f, TextUnitType.Sp),
        lineHeight = TextUnit(11f, TextUnitType.Sp),
        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp)
    )
)


@Composable
fun EcoTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography
    ) {
        content()
    }
}