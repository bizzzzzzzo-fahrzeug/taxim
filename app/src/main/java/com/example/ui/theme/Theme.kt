package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
  lightColorScheme(
    primary = SleekBrandPurple,
    secondary = SleekPurpleLightBg,
    tertiary = SleekBlueLightBg,
    background = SleekBg,
    surface = SleekBg,
    onPrimary = Color.White,
    onSecondary = SleekPurpleDarkText,
    onTertiary = SleekBlueDarkText,
    onBackground = SleekTextDark,
    onSurface = SleekTextDark,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Set to false by default for light theme Sleek experience
  // Disable dynamic color so we display our bespoke handcrafted theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
