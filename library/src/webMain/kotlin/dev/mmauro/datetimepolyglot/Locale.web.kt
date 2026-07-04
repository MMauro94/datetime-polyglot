package dev.mmauro.datetimepolyglot

import js.intl.Locale
import js.intl.LocaleMatcher
import js.intl.NumberFormat
import js.intl.bestFit
import js.objects.unsafeJso

internal actual typealias PlatformLocale = Locale

internal actual fun getDefaultLocale(): Locale = localeFromBcp47LanguageTag(NumberFormat().resolvedOptions().locale)

internal actual val PlatformLocale.bcp47LanguageTag: String
    get() = this.toString()

internal actual val PlatformLocale.baseLocale: PlatformLocale
    get() = localeFromBcp47LanguageTag(this.baseName)