package dev.mmauro.datetimepolyglot

import js.intl.Locale

internal actual fun localeFromString(locale: String) = Locale(locale)