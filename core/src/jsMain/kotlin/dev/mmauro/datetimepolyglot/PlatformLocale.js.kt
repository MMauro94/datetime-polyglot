package dev.mmauro.datetimepolyglot

import js.intl.Locale

public actual fun getLocaleFromTag(tag: String): Locale = Locale(tag)
