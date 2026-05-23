package dev.mmauro.datetimepolyglot

interface DateTimeZonedLocalizer<T> {

    fun localize(value: Zoned<T>): String
}