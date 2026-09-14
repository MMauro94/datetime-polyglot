package dev.mmauro.datetimepolyglot

import java.util.Locale

public actual typealias PlatformLocale = Locale

public actual fun getDefaultLocale(): PlatformLocale = Locale.getDefault()
