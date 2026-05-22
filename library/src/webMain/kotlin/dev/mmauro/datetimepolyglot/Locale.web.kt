package dev.mmauro.datetimepolyglot

import js.intl.Locale
import js.intl.NumberFormat

actual typealias PlatformLocale = Locale

actual fun getDefaultLocale(): Locale = localeFromString(NumberFormat().resolvedOptions().locale)

expect fun localeFromString(locale: String): Locale