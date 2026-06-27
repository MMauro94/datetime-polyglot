package dev.mmauro.datetimepolyglot

internal expect val LOCALE_ENGLISH: PlatformLocale

expect class PlatformLocale

internal expect fun getDefaultLocale(): PlatformLocale
