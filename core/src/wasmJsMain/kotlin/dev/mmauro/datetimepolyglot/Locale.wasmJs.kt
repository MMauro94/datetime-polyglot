package dev.mmauro.datetimepolyglot

import js.intl.Locale

@OptIn(ExperimentalWasmJsInterop::class)
public actual fun getLocaleFromTag(tag: String): Locale = Locale(tag.toJsString())
