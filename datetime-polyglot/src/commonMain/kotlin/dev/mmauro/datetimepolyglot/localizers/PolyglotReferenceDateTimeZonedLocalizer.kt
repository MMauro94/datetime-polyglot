package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import kotlin.time.Instant

/**
 * Specialization of [PolyglotReferenceDateTimeLocalizer] that allows to localize a [Zoned] date/time value [T] and returns a
 * [TickingValue]<[String]>.
 *
 * A date/time value is any component or set of components that may appear in a full date time localization (e.g. year, minute of hour,
 * time zone, day of the week, etc.)
 *
 * Note that any class implementing this interface is **not** strictly required to return the same exact string for the same value in
 * different platforms.
 *
 * @see PolyglotReferenceDateTimeLocalizer
 */
public interface PolyglotReferenceDateTimeZonedLocalizer<in T> : PolyglotReferenceDateTimeLocalizer<Zoned<T>>
