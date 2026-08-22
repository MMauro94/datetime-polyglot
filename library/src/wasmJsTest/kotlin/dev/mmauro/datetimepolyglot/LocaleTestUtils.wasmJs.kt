@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.mmauro.datetimepolyglot

// JS doesn't expose a list of locales, let's just provide short list for now
actual val ALL_LOCALES get() = listOf(LOCALE_ENGLISH, LOCALE_ITALIAN, LOCALE_POLISH)
