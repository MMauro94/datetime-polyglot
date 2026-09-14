package dev.mmauro.datetimepolyglot.compose.utils

import androidx.compose.ui.text.intl.Locale
import dev.mmauro.datetimepolyglot.PlatformLocale

internal expect fun Locale.toPlatformLocale(): PlatformLocale
