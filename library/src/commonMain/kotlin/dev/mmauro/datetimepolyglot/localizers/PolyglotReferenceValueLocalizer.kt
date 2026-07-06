package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.withNextTickAtMost
import dev.mmauro.datetimepolyglot.zonedNow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * A generic [PolyglotLocalizer] that accepts a value of type [T] and a [Zoned]<[Instant]> reference point, and returns a [TickingValue] of
 * type [String].
 *
 * The [TickingValue.nextTick] represent the amount of time that the reference needs to advance by for the [TickingValue.value] to change,
 * thus requiring a new call to [localize] with the same exact value but updated reference, assuming that the time moves forward linearly.
 *
 * If you are using coroutines, a convenience [localizeAsFlow] is also provided.
 *
 * Note that any class implementing this interface is **not** strictly required to return the same exact string for the same value in
 * different platforms.
 */
interface PolyglotReferenceValueLocalizer<in T> : PolyglotLocalizer {
    fun localize(value: T, reference: Zoned<Instant>): TickingValue<String>
}

/**
 * Localizes [value] by using the given [clock]'s [Clock.now] as a reference point, rather than passing the reference explicitly.
 */
fun <T> PolyglotReferenceValueLocalizer<T>.localize(value: T, clock: Clock = Clock.System): TickingValue<String> {
    return localize(value, clock.zonedNow())
}

/**
 * Localizes [value] by using the given [clock]'s [Clock.now] as a reference point and returns a [Flow] that emits updated values based on
 * the underlying [TickingValue.nextTick]. This function assumes that the given [clock] moves forward linearly.
 *
 * The flow will automatically complete as soon as a localization returns `null` as [TickingValue.nextTick], as that means that the
 * localization is now "stable" and will not require further passes.
 *
 * Important: please note that this function does **not** track changes on the given [clock]. If the clock changes (e.g. because of a NTP
 * sync or manual intervention), this function will not recompute the value immediately, but wait for the normal amount of time as returned
 * by [TickingValue.nextTick]. This might cause the localized value to be stale for a certain amount of time.
 * This should not normally be a problem given that usually adjustments are in the order of milliseconds.
 * If this is a concern for you can either track the clock changes in your platform and subscribe to a fresh [Flow] when it happens, or
 * provide a [maxTick] parameter, which guarantees a fresh computation at least every [maxTick].
 * Note that, as [Clock] is a source of [Instant]s (and not local values), DST changes are **not** affected by this, and will work
 * correctly.
 *
 * @param value the value to localized, passed as-is to [localize]
 * @param clock the clock to use to obtain the reference point
 * @param maxTick the maximum amount of to wait for a recomputation (as long as the underlying [localize] returns a non-null
 * [TickingValue.nextTick]). If null (the default), no max tick bound is applied.
 */
fun <T> PolyglotReferenceValueLocalizer<T>.localizeAsFlow(
    value: T,
    clock: Clock = Clock.System,
    maxTick: Duration? = null,
): Flow<String> {
    return flow {
        var last: TickingValue<String>

        do {
            last = localize(value, clock)
            if (last.nextTick != null) {
                // If the returned maxTick is null, it means that the localized string is not affected by the reference point anymore, so it
                // doesn't make sense to keep applying maxTick in this case
                last = last.withNextTickAtMost(maxTick)
            }
            emit(last.value)

            if (last.nextTick != null) {
                delay(last.nextTick)
            }
        } while (last.nextTick != null)
    }
}