package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.ClockWrapper
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.withNextTickAtMost
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.transformLatest
import kotlinx.datetime.TimeZone
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
public interface PolyglotReferenceValueLocalizer<in T> : PolyglotLocalizer {
    public fun localize(value: T, reference: Zoned<Instant>): TickingValue<String>
}

/**
 * Localizes [value] by using the given [clock]'s [Clock.now] and [timeZone] as a reference point, rather than passing the reference
 * explicitly.
 */
public fun <T> PolyglotReferenceValueLocalizer<T>.localizeNow(
    value: T,
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): TickingValue<String> {
    return localize(value, Zoned(clock.now(), timeZone))
}

/**
 * Localizes [value] by using the given [clock]'s [Clock.now] and [timeZone] flows as a reference point and returns a [Flow] that emits
 * updated values based on the underlying [TickingValue.nextTick].
 * This function assumes that each [Clock] emitted by the [clock] flow moves forward linearly.
 *
 * The flow always immediately emits the localized value before any suspension point, assuming that [clock] and [timeZone] immediately emit
 * a value as well.
 *
 * @param value the value to localized, passed as-is to [PolyglotReferenceValueLocalizer.localize]
 * @param clock the [Clock] to use to obtain the [Instant] for the reference point. It is a [Flow] because the time might be changed (e.g.
 * by an NTP sync or by the user manually adjusting the time). Whenever a new [ClockWrapper] is emitted, the localized string is recomputed.
 * See [SYSTEM_CLOCK] for more info.
 * @param timeZone the [TimeZone] to use for the reference point. It is a [Flow] because it might change (e.g. if the user crosses a
 * time zone line). When a different time zone is emitted, the localized string is recomputed. See [SYSTEM_TIMEZONE] for more info.
 * @param maxTick the maximum amount of to wait for a recomputation (as long as the underlying [PolyglotReferenceValueLocalizer.localize]
 * returns a non-null [TickingValue.nextTick]). If null (the default), no max tick bound is applied.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public fun <T> PolyglotReferenceValueLocalizer<T>.localizeAsFlow(
    value: T,
    clock: Flow<ClockWrapper> = SYSTEM_CLOCK,
    timeZone: Flow<TimeZone> = SYSTEM_TIMEZONE,
    maxTick: Duration? = null,
): Flow<String> {
    return tickingValueToFlow(clock, timeZone, maxTick, tickingValueProvider = { reference -> localize(value, reference) })
}

/**
 * Uses the given [clock] and [timeZone] flows to compute the reference point passed to [tickingValueProvider] and returns the resulting
 * [Flow]. This function assumes that each [Clock] emitted by the [clock] flow moves forward linearly.
 *
 * This function can be used to bridge any function returning a [TickingValue] to use a [Flow].
 *
 * The flow always immediately emits the value computed by [tickingValueProvider] before any suspension point, assuming that [clock] and
 * [timeZone] immediately emit a value as well.
 *
 * This is the primitive that powers [localizeAsFlow].
 *
 * @param clock the [Clock] to use to obtain the [Instant] for the reference point. It is a [Flow] because the time might be changed (e.g.
 * by an NTP sync or by the user manually adjusting the time). Whenever a new [ClockWrapper] is emitted, the localized string is recomputed.
 * See [SYSTEM_CLOCK] for more info.
 * @param timeZone the [TimeZone] to use for the reference point. It is a [Flow] because it might change (e.g. if the user crosses a
 * time zone line). When a different time zone is emitted, the localized string is recomputed. See [SYSTEM_TIMEZONE] for more info.
 * @param maxTick the maximum amount of to wait for a recomputation (as long as the underlying [tickingValueProvider] returns a non-null
 * [TickingValue.nextTick]). If null (the default), no max tick bound is applied.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public fun <T> tickingValueToFlow(
    clock: Flow<ClockWrapper> = SYSTEM_CLOCK,
    timeZone: Flow<TimeZone> = SYSTEM_TIMEZONE,
    maxTick: Duration? = null,
    tickingValueProvider: (reference: Zoned<Instant>) -> TickingValue<T>,
): Flow<T> {
    return combine(clock, timeZone.distinctUntilChanged(), ::Pair).transformLatest { (clock, timeZone) ->
        var last: TickingValue<T>

        do {
            last = tickingValueProvider(Zoned(clock.clock.now(), timeZone))
            if (last.nextTick != null) {
                // If the returned maxTick is null, it means that the localized string is not affected by the reference point anymore,
                // so it doesn't make sense to keep applying maxTick in this case
                last = last.withNextTickAtMost(maxTick)
            }
            emit(last.value)

            if (last.nextTick != null) {
                delay(last.nextTick)
            }
        } while (last.nextTick != null)
    }
}
