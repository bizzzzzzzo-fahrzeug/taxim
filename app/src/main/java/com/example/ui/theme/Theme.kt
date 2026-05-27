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

private val DarkColorScheme = darkColorScheme(
  primary = TaxiYellow,
  onPrimary = CharcoalDark,
  primaryContainer = TaxiYellowDark,
  secondary = CharcoalSurface,
  onSecondary = PureWhite,
  tertiary = TaxiAmber,
  background = CharcoalDark,
  surface = CharcoalSurface,
  onBackground = PureWhite,
  onSurface = PureWhite,
  error = ErrorRed,
  onError = PureWhite,
)

private val LightColorScheme = lightColorScheme(
  primary = TaxiYellow,
  onPrimary = CharcoalDark,
  primaryContainer = YellowBg,
  secondary = CharcoalDark,
  onSecondary = PureWhite,
  secondaryContainer = GrayBg,
  tertiary = TaxiAmber,
  background = OffWhite,
  surface = PureWhite,
  surfaceVariant = WarmGray,
  onBackground = CharcoalDark,
  onSurface = CharcoalDark,
  onSurfaceVariant = SlateGray,
  outline = BorderLight,
  outlineVariant = BorderMedium,
  error = ErrorRed,
  onError = PureWhite,
  errorContainer = AlertBg,
  onErrorContainer = ErrorRed,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
