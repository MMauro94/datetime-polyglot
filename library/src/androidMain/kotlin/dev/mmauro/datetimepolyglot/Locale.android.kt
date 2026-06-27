package dev.mmauro.datetimepolyglot

import android.icu.util.ULocale

internal actual val LOCALE_ENGLISH = ULocale.ENGLISH

internal actual typealias PlatformLocale = ULocale

internal actual fun getDefaultLocale() = ULocale.getDefault()