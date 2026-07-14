package dev.mmauro.datetimepolyglot

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Hodler for a generic value [T] that is associated with a [TimeZone]
 */
data class Zoned<out T>(
    val value: T,
    val timeZone: TimeZone
)

/**
 * Returns a civil datetime value that this instant [Zoned.value] has in the specified [Zoned.timeZone].
 *
 * @see Instant.toLocalDateTime
 */
fun Zoned<Instant>.toLocalDateTime() = value.toLocalDateTime(timeZone)

operator fun Zoned<Instant>.plus(duration: Duration) = Zoned(value + duration, timeZone)
operator fun Zoned<Instant>.minus(duration: Duration) = Zoned(value - duration, timeZone)

/**
 * Returns a [Zoned]<[Instant]> using this [Clock] as source and [TimeZone.currentSystemDefault] for timezone.
 */
fun Clock.zonedNow(): Zoned<Instant> {
    return Zoned(
        value = now(),
        timeZone = TimeZone.currentSystemDefault(),
    )
}

/**
 * Returns a new [Zoned] with the same [Zoned.timeZone] and the value returned by [transform].
 */
fun <T, R> Zoned<T>.mapValue(transform: (T) -> R): Zoned<R> {
    return Zoned(transform(value), this.timeZone)
}