package dev.mmauro.datetimepolyglot

import com.ibm.icu.util.ULocale

internal actual val LOCALE_ENGLISH = PlatformLocale.ENGLISH

internal actual typealias PlatformLocale = ULocale

internal actual fun getDefaultLocale() = ULocale.getDefault()