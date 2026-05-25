package dev.mmauro.datetimepolyglot

import android.icu.util.ULocale

internal actual typealias PlatformLocale = ULocale

internal actual fun getDefaultLocale() = ULocale.getDefault()