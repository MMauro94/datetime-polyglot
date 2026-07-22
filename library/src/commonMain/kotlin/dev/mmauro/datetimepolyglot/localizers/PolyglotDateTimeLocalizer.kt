package dev.mmauro.datetimepolyglot.localizers

/**
 * Specialization of [PolyglotValueLocalizer] that allows to localize a date/time value [T] and returns a simple [String].
 *
 * A date/time value is any component or set of components that may appear in a full date time localization (e.g. year, minute of hour,
 * time zone, day of the week, etc.)
 *
 * Note that any class implementing this interface is **not** strictly required to return the same exact string for the same value in
 * different platforms.
 */
interface PolyglotDateTimeLocalizer<in T> : PolyglotValueLocalizer<T, String>