package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.ClockWrapper
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.TickingValueProvider
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.toFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * A generic [PolyglotLocalizer] that accepts a value of type [T] and a [Zoned]<[Instant]> reference point, and returns a [TickingValue] of
 * type [R].
 *
 * The [TickingValue.nextTick] represent the amount of time that the reference needs to advance by for the [TickingValue.value] to change,
 * thus requiring a new call to [localize] with the same exact value but updated reference, assuming that the time moves forward linearly.
 *
 * If you are using coroutines, a convenience [localizeAsFlow] is also provided.
 *
 * Note that any class implementing this interface is **not** strictly required to return the same exact string for the same value in
 * different platforms.
 */
public interface PolyglotReferenceValueLocalizer<in T, out R> : PolyglotLocalizer {
    public fun localize(value: T, reference: Zoned<Instant>): TickingValue<R>
}

/**
 * Fixes the [value] to localize and returns a [TickingValueProvider] using this [PolyglotReferenceValueLocalizer] as source for
 * [TickingValue]s.
 */
public fun <T, R> PolyglotReferenceValueLocalizer<T, R>.toTickingValueProvider(value: T): TickingValueProvider<R> {
    return TickingValueProvider { reference -> localize(value, reference) }
}

/**
 * Localizes [value] by using the given [clock]'s [Clock.now] and [timeZone] as a reference point, rather than passing the reference
 * explicitly.
 */
public fun <T, R> PolyglotReferenceValueLocalizer<T, R>.localizeNow(
    value: T,
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): TickingValue<R> {
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
 * @param value the value to localized, passed as-is to [PolyglotReferenceDateTimeLocalizer.localize]
 * @param clock the [Clock] to use to obtain the [Instant] for the reference point. It is a [Flow] because the time might be changed (e.g.
 * by an NTP sync or by the user manually adjusting the time). Whenever a new [ClockWrapper] is emitted, the localized string is recomputed.
 * See [SYSTEM_CLOCK] for more info.
 * @param timeZone the [TimeZone] to use for the reference point. It is a [Flow] because it might change (e.g. if the user crosses a
 * time zone line). When a different time zone is emitted, the localized string is recomputed. See [SYSTEM_TIMEZONE] for more info.
 * @param maxTick the maximum amount of to wait for a recomputation (as long as the underlying [PolyglotReferenceDateTimeLocalizer.localize]
 * returns a non-null [TickingValue.nextTick]). If null (the default), no max tick bound is applied.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public fun <T, R> PolyglotReferenceValueLocalizer<T, R>.localizeAsFlow(
    value: T,
    clock: Flow<ClockWrapper> = SYSTEM_CLOCK,
    timeZone: Flow<TimeZone> = SYSTEM_TIMEZONE,
    maxTick: Duration? = null,
): Flow<R> {
    return toTickingValueProvider(value).toFlow(clock, timeZone, maxTick)
}
