package dev.mmauro.datetimepolyglot.compose.utils

import dev.mmauro.datetimepolyglot.getLocaleFromTag
import androidx.compose.ui.text.intl.Locale as ComposeLocale

internal actual fun ComposeLocale.toPlatformLocale() = getLocaleFromTag(toLanguageTag())
