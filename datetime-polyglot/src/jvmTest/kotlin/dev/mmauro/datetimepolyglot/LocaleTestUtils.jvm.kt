package dev.mmauro.datetimepolyglot

import com.ibm.icu.util.ULocale

actual val ALL_LOCALES = ULocale.getAvailableLocales().map { it.toLocale() }
