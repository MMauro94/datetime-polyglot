package dev.mmauro.datetimepolyglot

import js.intl.Locale
import js.intl.NumberFormat

public actual typealias PlatformLocale = Locale

@OptIn(ExperimentalWasmJsInterop::class)
public actual fun getDefaultLocale(): Locale = Locale(NumberFormat().resolvedOptions().locale.toJsString())
