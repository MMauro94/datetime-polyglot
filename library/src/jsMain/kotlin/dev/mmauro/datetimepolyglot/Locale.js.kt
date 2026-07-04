package dev.mmauro.datetimepolyglot

import js.intl.Locale

internal actual val LOCALE_ENGLISH = PlatformLocale("en")

internal actual fun localeFromBcp47LanguageTag(tag: String) = Locale(tag)