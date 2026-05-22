package dev.mmauro.datetimepolyglot

import js.intl.Locale

actual fun localeFromString(locale: String) = Locale(locale)