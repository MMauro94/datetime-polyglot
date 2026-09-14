package dev.mmauro.datetimepolyglot

import android.icu.util.ULocale

public actual typealias PlatformLocale = ULocale

public actual fun getDefaultLocale(): ULocale = ULocale.getDefault()

public actual fun getLocaleFromTag(tag: String): PlatformLocale {
    return ULocale.forLanguageTag(tag)
}
