package dev.mmauro.datetimepolyglot

import com.ibm.icu.util.ULocale

actual typealias PlatformLocale = ULocale

actual fun getDefaultLocale() = ULocale.getDefault()