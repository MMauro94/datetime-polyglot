package dev.mmauro.datetimepolyglot

expect class SimpleDateFormat(pattern: String, locale: PlatformLocale) {
    fun format(temporal: Any): String
}