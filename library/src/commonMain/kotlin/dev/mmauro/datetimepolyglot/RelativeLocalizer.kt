package dev.mmauro.datetimepolyglot

/**
 * Class that allows to localize something in a relative way.
 *
 * Note that, similarly to [DateTimeLocalizer], any class implementing this interface is **not** strictly required to return the same exact
 * value for the same value in different platforms.
 *
 * @see TickingValue
 */
interface RelativeLocalizer<T> {
    fun localize(value: T): TickingValue<String>
}
