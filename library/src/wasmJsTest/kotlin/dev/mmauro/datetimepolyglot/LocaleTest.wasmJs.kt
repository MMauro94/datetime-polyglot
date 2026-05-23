@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.mmauro.datetimepolyglot

actual val LOCALE_ENGLISH = PlatformLocale("en".toJsString())
actual val LOCALE_ITALIAN = PlatformLocale("it".toJsString())
actual val LOCALE_POLISH = PlatformLocale("pl".toJsString())