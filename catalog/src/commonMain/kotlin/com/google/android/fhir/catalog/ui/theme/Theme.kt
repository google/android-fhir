/*
 * Copyright 2025-2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.catalog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryBlue80,
    onPrimary = OnPrimaryBlue20,
    primaryContainer = PrimaryContainerBlue30,
    onPrimaryContainer = OnPrimaryContainerBlue90,
    secondary = SecondaryBlue80,
    onSecondary = OnSecondaryBlue20,
    secondaryContainer = SecondaryContainerBlue30,
    onSecondaryContainer = OnSecondaryContainerBlue90,
    tertiary = TertiaryGreen80,
    onTertiary = OnTertiaryGreen20,
    tertiaryContainer = TertiaryContainerGreen30,
    onTertiaryContainer = OnTertiaryContainerGreen90,
    error = ErrorRed80,
    errorContainer = ErrorContainerRed20,
    onError = OnErrorRed30,
    onErrorContainer = OnErrorContainerRed90,
    background = BackgroundNeutral10,
    onBackground = OnBackgroundNeutral90,
    surface = SurfaceNeutral10,
    onSurface = OnSurfaceNeutral90,
    surfaceVariant = SurfaceVariantNeutralVariant30,
    onSurfaceVariant = OnSurfaceVariantNeutralVariant80,
    outline = OutlineNeutralVariant60,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryBlue40,
    onPrimary = OnPrimaryBlue100,
    primaryContainer = PrimaryContainerBlue90,
    onPrimaryContainer = OnPrimaryContainerBlue10,
    secondary = SecondaryBlue40,
    onSecondary = OnSecondaryBlue100,
    secondaryContainer = SecondaryContainerBlue90,
    onSecondaryContainer = OnSecondaryContainerBlue10,
    tertiary = TertiaryGreen40,
    onTertiary = OnTertiaryGreen100,
    tertiaryContainer = TertiaryContainerGreen90,
    onTertiaryContainer = OnTertiaryContainerGreen10,
    error = ErrorRed40,
    errorContainer = ErrorContainerRed100,
    onError = OnErrorRed90,
    onErrorContainer = OnErrorContainerRed10,
    background = BackgroundNeutral100,
    onBackground = OnBackgroundNeutral10,
    surface = SurfaceNeutral100,
    onSurface = OnSurfaceNeutral10,
    surfaceVariant = SurfaceVariantNeutralVariant90,
    onSurfaceVariant = OnSurfaceVariantNeutralVariant30,
    outline = OutlineNeutralVariant50,
  )

@Composable
fun AppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    content = content,
  )
}
