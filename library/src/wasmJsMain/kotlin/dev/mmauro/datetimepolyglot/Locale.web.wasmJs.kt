package dev.mmauro.datetimepolyglot

import js.intl.Locale

@OptIn(ExperimentalWasmJsInterop::class)
internal actual val LOCALE_ENGLISH = PlatformLocale("en".toJsString())

@OptIn(ExperimentalWasmJsInterop::class)
internal actual fun localeFromString(locale: String) = Locale(locale.toJsString())