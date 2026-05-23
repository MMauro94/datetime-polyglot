package dev.mmauro.datetimepolyglot

interface DateTimeLocalizer<T> {

    fun localize(value: T): String
}