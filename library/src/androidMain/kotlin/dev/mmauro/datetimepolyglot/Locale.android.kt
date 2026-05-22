package dev.mmauro.datetimepolyglot

import android.icu.util.ULocale

actual typealias PlatformLocale = ULocale

actual fun getDefaultLocale() = ULocale.getDefault()