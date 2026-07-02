package dev.mmauro.datetimepolyglot

import com.ibm.icu.util.ULocale

actual val ALL_LOCALES = ULocale.getAvailableLocales().toList()

actual fun getLocale(name: String) = ULocale(name)