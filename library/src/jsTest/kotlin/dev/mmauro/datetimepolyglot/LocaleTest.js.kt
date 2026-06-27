package dev.mmauro.datetimepolyglot

actual val LOCALE_ITALIAN = PlatformLocale("it")
actual val LOCALE_POLISH = PlatformLocale("pl")

// JS doesn't expose a list of locales, let's just provide short list for now
actual val ALL_LOCALES = listOf(LOCALE_ITALIAN, LOCALE_ITALIAN, LOCALE_POLISH)