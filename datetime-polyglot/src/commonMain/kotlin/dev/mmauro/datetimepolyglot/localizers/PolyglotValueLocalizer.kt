package dev.mmauro.datetimepolyglot.localizers

/**
 * A generic [PolyglotLocalizer] that accepts a just value of type [T] and returns a result of type [R].
 *
 * Note that any class implementing this interface is **not** strictly required to return the same exact string for the same value in
 * different platforms.
 *
 * @see PolyglotDateTimeLocalizer
 * @see PolyglotDateTimeZonedLocalizer
 */
public interface PolyglotValueLocalizer<in T, out R> : PolyglotLocalizer {
    public fun localize(value: T): R
}
