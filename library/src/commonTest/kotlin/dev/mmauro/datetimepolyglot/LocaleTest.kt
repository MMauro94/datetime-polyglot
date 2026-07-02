package dev.mmauro.datetimepolyglot

val LOCALE_ITALIAN = getLocale("it")
val LOCALE_POLISH = getLocale("pl")

expect val ALL_LOCALES: List<PlatformLocale>

expect fun getLocale(name: String): PlatformLocale