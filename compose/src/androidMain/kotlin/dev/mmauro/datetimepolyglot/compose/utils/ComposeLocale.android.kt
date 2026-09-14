package dev.mmauro.datetimepolyglot.compose.utils

import android.icu.util.ULocale
import dev.mmauro.datetimepolyglot.PlatformLocale
import androidx.compose.ui.text.intl.Locale as ComposeLocale
import java.util.Locale as JavaLocale

internal actual fun ComposeLocale.toPlatformLocale(): PlatformLocale = platformLocale.toULocale()

private fun JavaLocale.toULocale() = ULocale.forLanguageTag(toLanguageTag())
