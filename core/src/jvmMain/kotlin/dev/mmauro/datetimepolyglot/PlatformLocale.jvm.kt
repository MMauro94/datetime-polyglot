package dev.mmauro.datetimepolyglot

import java.util.Locale

public actual typealias PlatformLocale = Locale

public actual fun getDefaultLocale(): PlatformLocale = Locale.getDefault()

public actual fun getLocaleFromTag(tag: String): PlatformLocale {
    return PlatformLocale.forLanguageTag(tag)
}
