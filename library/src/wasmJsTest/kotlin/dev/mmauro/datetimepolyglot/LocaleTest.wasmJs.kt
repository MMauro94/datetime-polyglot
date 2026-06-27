@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.mmauro.datetimepolyglot

actual val LOCALE_ITALIAN = PlatformLocale("it".toJsString())
actual val LOCALE_POLISH = PlatformLocale("pl".toJsString())

// JS doesn't expose a list of locales, let's just provide short list for now
actual val ALL_LOCALES = listOf(LOCALE_ITALIAN, LOCALE_ITALIAN, LOCALE_POLISH)