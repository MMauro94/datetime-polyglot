package dev.mmauro.datetimepolyglot

import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * This class holds a value of type [T] and a [Duration] that indicates how much time needs to pass before recalculating [value] is
 * required (i.e. how long since its production [nextTick] is valid for).
 *
 * Note: this class purposefully does track the date/time when the [value] "expires", but simply provides a [Duration] indicating how long
 * the [value] is valid for. It is a responsibility of callers to keep track when the [value] needs to be recomputed.
 * For this reason, storing or transmitting this class via methods that require a significant delay (e.g. over a network) is not
 * recommended.
 *
 * @param value the value
 * @param nextTick after how much time the next value should be re-computed. `null` means that no more computations are needed.
 */
public data class TickingValue<out T>(val value: T, val nextTick: Duration?) {
    init {
        if (nextTick != null) {
            require(nextTick.isPositive()) { "next tick must be positive ($nextTick)" }
            require(nextTick.isFinite()) { "next tick must be finite ($nextTick)" }
        }
    }

    /**
     * Waits the required amount of time before `this` [TickingValue] needs recalculation, depending on the current time provided by
     * [clock].
     *
     * If the current time is past the time for recomputation, no amount of time is waited. Same when [nextTick] is `null`.
     *
     * @param reference the reference point used to calculate this value
     * @param clock the clock used to determine the current time
     * @param maxTick the maximum amount of to wait for a recomputation (as long as [nextTick] is not `null`).
     * If `null` (the default), no max tick bound is applied.
     *
     * @return `false` if [nextTick] is null, `true` otherwise
     */
    public suspend fun delay(
        reference: Instant,
        clock: Clock,
        maxTick: Duration? = null,
    ): Boolean {
        return if (nextTick != null) {
            // There might have been a small amount of time between when the nextTick was calculated and now due to slowness of provide() or
            // scheduling delays of the coroutines.
            // For this reason, we compute this offset here to account for any time lost due to that, and subtract it from the delay time.
            val offset = clock.now() - reference
            val organicDelay = nextTick - offset
            delay(if (maxTick == null) organicDelay else minOf(organicDelay, maxTick))
            true
        } else {
            false
        }
    }

    public suspend fun delay(
        reference: Zoned<Instant>,
        clock: Clock,
        maxTick: Duration? = null,
    ): Boolean = delay(reference.value, clock, maxTick = maxTick)
}

/**
 * Returns a [TickingValue] with the same [TickingValue.value] and with the next tick being the min of its value and [TickingValue.nextTick].
 *
 * Passing a `null` [nextTick] will return a [TickingValue] equivalent to this.
 */
public fun <T> TickingValue<T>.withNextTickAtMost(nextTick: Duration?): TickingValue<T> = TickingValue(
    value = value,
    nextTick = listOfNotNull(this.nextTick, nextTick).minOrNull(),
)

/**
 * Transforms the [TickingValue.value] from this instance from type [T] to [R] using [transform] and returns a new [TickingValue] with it
 * and the same [TickingValue.nextTick].
 */
public fun <T, R> TickingValue<T>.map(transform: (T) -> R): TickingValue<R> {
    return TickingValue(value = transform(value), nextTick = nextTick)
}

/**
 * Transforms this [TickingValue] using the [transform] function.
 * The resulting [TickingValue.nextTick] will be the min of the two, see also [withNextTickAtMost].
 */
public fun <T, R> TickingValue<T>.flatMap(transform: (T) -> TickingValue<R>): TickingValue<R> {
    return transform(value).withNextTickAtMost(nextTick)
}

/**
 * Combines two different [TickingValue]s.
 *
 * The resulting [TickingValue.value] is computed by [transform], which receives the value of this and [other].
 * The resulting [TickingValue.nextTick] will be the min of this and [other], see also [withNextTickAtMost].
 */
public fun <T1, T2, R> TickingValue<T1>.combine(
    other: TickingValue<T2>,
    transform: (T1, T2) -> R,
): TickingValue<R> {
    return TickingValue(
        value = transform(this.value, other.value),
        nextTick = listOfNotNull(this.nextTick, other.nextTick).minOrNull(),
    )
}
