package dev.mmauro.datetimepolyglot

import com.ibm.icu.util.ULocale

internal actual typealias PlatformLocale = ULocale

internal actual fun getDefaultLocale() = ULocale.getDefault()