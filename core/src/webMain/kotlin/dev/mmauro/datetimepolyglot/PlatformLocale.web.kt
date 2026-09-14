package dev.mmauro.datetimepolyglot

import js.intl.Locale
import js.intl.NumberFormat

public actual typealias PlatformLocale = Locale

public actual fun getDefaultLocale(): Locale = getLocaleFromTag(NumberFormat().resolvedOptions().locale)
