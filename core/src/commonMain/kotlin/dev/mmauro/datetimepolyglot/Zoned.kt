package dev.mmauro.datetimepolyglot

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Hodler for a generic value [T] that is associated with a [TimeZone]
 */
public data class Zoned<out T>(
    val value: T,
    val timeZone: TimeZone,
)

/**
 * Returns a civil datetime value that this instant [Zoned.value] has in the specified [Zoned.timeZone].
 *
 * @see Instant.toLocalDateTime
 */
public fun Zoned<Instant>.toLocalDateTime(): LocalDateTime = value.toLocalDateTime(timeZone)

public operator fun Zoned<Instant>.plus(duration: Duration): Zoned<Instant> = Zoned(value + duration, timeZone)
public operator fun Zoned<Instant>.minus(duration: Duration): Zoned<Instant> = Zoned(value - duration, timeZone)

/**
 * Returns a new [Zoned] with the same [Zoned.timeZone] and the value returned by [transform].
 */
public fun <T, R> Zoned<T>.mapValue(transform: (T) -> R): Zoned<R> {
    return Zoned(transform(value), this.timeZone)
}
