package dev.mmauro.datetimepolyglot

import js.intl.Locale
import js.intl.NumberFormat

internal actual typealias PlatformLocale = Locale

internal actual fun getDefaultLocale(): Locale = localeFromString(NumberFormat().resolvedOptions().locale)

internal expect fun localeFromString(locale: String): Locale