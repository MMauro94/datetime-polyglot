package dev.mmauro.datetimepolyglot

import kotlin.time.Duration

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
data class TickingValue<out T>(val value: T, val nextTick: Duration?) {
    init {
        if (nextTick != null) {
            require(nextTick.isPositive()) { "next tick must be positive ($nextTick)" }
            require(nextTick.isFinite()) { "next tick must be finite ($nextTick)" }
        }
    }
}

/**
 * Returns a [TickingValue] with the same [TickingValue.value] and with the next tick being the min of its value and [nextTick].
 *
 * Passing a `null` [nextTick] will return a [TickingValue] equivalent to this.
 */
fun <T> TickingValue<T>.withNextTickAtMost(nextTick: Duration?) = TickingValue(
    value = value,
    nextTick = listOfNotNull(this.nextTick, nextTick).minOrNull(),
)

/**
 * Transforms the [TickingValue.value] from this instance from type [T] to [R] using [transform] and returns a new [TickingValue] with it
 * and the same [TickingValue.nextTick].
 */
fun <T, R> TickingValue<T>.map(transform: (T) -> R): TickingValue<R> {
    return TickingValue(value = transform(value), nextTick = nextTick)
}

/**
 * Transforms this [TickingValue] using the [transform] function.
 * The resulting [TickingValue.nextTick] will be the min of the two, see also [withNextTickAtMost].
 */
fun <T, R> TickingValue<T>.flatMap(transform: (T) -> TickingValue<R>): TickingValue<R> {
    return transform(value).withNextTickAtMost(nextTick)
}

/**
 * Combines two different [TickingValue]s.
 *
 * The resulting [TickingValue.value] is computed by [transform], which receives the value of this and [other].
 * The resulting [TickingValue.nextTick] will be the min of this and [other], see also [withNextTickAtMost].
 */
fun <T1, T2, R> TickingValue<T1>.combine(
    other: TickingValue<T2>,
    transform: (T1, T2) -> R,
): TickingValue<R> {
    return TickingValue(
        value = transform(this.value, other.value),
        nextTick = listOfNotNull(this.nextTick, other.nextTick).minOrNull(),
    )
}


