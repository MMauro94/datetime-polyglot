package dev.mmauro.datetimepolyglot.compose.utils

import dev.mmauro.datetimepolyglot.PlatformLocale
import androidx.compose.ui.text.intl.Locale as ComposeLocale

internal actual fun ComposeLocale.toPlatformLocale(): PlatformLocale = platformLocale
