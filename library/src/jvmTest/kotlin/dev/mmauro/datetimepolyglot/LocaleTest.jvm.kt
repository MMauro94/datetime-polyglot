package dev.mmauro.datetimepolyglot

import com.ibm.icu.util.ULocale

actual val LOCALE_ITALIAN = PlatformLocale.ITALIAN
actual val LOCALE_POLISH = PlatformLocale("pl")
actual val ALL_LOCALES = ULocale.getAvailableLocales().toList()