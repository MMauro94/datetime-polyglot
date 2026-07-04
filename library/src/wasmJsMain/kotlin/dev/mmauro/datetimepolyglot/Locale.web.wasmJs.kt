package dev.mmauro.datetimepolyglot

import js.intl.Locale

@OptIn(ExperimentalWasmJsInterop::class)
internal actual val LOCALE_ENGLISH = PlatformLocale("en".toJsString())

@OptIn(ExperimentalWasmJsInterop::class)
internal actual fun localeFromBcp47LanguageTag(tag: String) = Locale(tag.toJsString())