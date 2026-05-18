package uz.tubeme.app.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Lightweight i18n shim. We use Android string resources (values-uz / values-ru / values-en),
 * but the app stores user-selected language in DataStore and must apply it manually
 * via `LocaleHelper` (see MainActivity).
 */
val LocalLanguage = staticCompositionLocalOf { "uz" }

@Composable
fun lang(): String = LocalLanguage.current
