package dev.mmauro.datetimepolyglot

import android.icu.util.ULocale

actual val LOCALE_ITALIAN = ULocale.ITALIAN
actual val LOCALE_POLISH = ULocale("pl")
actual val ALL_LOCALES = ULocale.getAvailableLocales().toList()