package dev.mmauro.datetimepolyglot

import js.intl.Locale

@OptIn(ExperimentalWasmJsInterop::class)
internal actual fun localeFromString(locale: String) = Locale(locale.toJsString())