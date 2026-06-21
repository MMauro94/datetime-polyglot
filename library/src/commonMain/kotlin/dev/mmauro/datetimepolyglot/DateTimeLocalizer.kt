package dev.mmauro.datetimepolyglot

/**
 * Class that allows to localize a date/time component [T].
 *
 * Note that any class implementing this interface is **not** strictly required to return the same exact string for the same value in
 * different platforms.
 */
interface DateTimeLocalizer<T> {

    fun localize(value: T): String
}