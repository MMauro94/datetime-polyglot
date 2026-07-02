package dev.mmauro.datetimepolyglot

// JS doesn't expose a list of locales, let's just provide short list for now
actual val ALL_LOCALES = listOf(LOCALE_ITALIAN, LOCALE_ITALIAN, LOCALE_POLISH)

actual fun getLocale(name: String) = localeFromString(name)
