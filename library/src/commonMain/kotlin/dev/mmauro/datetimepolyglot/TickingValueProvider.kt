package dev.mmauro.datetimepolyglot

import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
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
 * Simple interface that allows implementors to [provide] a [TickingValue] for a given reference point.
 */
public fun interface TickingValueProvider<T> {
    public fun provide(reference: Zoned<Instant>): TickingValue<T>
}

/**
 * Simple mapping function that returns a new [TickingValueProvider] that, upon [TickingValueProvider.provide], calls [transform] with the
 * result of provide and its reference point, and returns the output of [transform].
 */
public fun <T, R> TickingValueProvider<T>.map(
    transform: (localized: TickingValue<T>, reference: Zoned<Instant>) -> TickingValue<R>,
): TickingValueProvider<R> {
    return TickingValueProvider { reference ->
        val localized = provide(reference)
        transform(localized, reference)
    }
}

/**
 * Uses the given [clock] and [timeZone] flows to compute the reference point passed to [TickingValueProvider.provide] and returns the
 * resulting [Flow]. This function assumes that each [Clock] emitted by the [clock] flow moves forward linearly.
 *
 * This is the primitive that powers [localizeAsFlow].
 *
 * @param clock the [Clock] to use to obtain the [Instant] for the reference point. It is a [Flow] because the time might be changed (e.g.
 * by an NTP sync or by the user manually adjusting the time). Whenever a new [ClockWrapper] is emitted, the localized string is recomputed.
 * See [SYSTEM_CLOCK] for more info.
 * @param timeZone the [TimeZone] to use for the reference point. It is a [Flow] because it might change (e.g. if the user crosses a
 * time zone line). When a different time zone is emitted, the localized string is recomputed. See [SYSTEM_TIMEZONE] for more info.
 * @param maxTick the maximum amount of to wait for a recomputation (as long as this underlying provider returns a non-null
 * [TickingValue.nextTick]). If null (the default), no max tick bound is applied.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public fun <T> TickingValueProvider<T>.toFlow(
    clock: Flow<ClockWrapper> = SYSTEM_CLOCK,
    timeZone: Flow<TimeZone> = SYSTEM_TIMEZONE,
    maxTick: Duration? = null,
): Flow<T> {
    return combine(clock, timeZone.distinctUntilChanged(), ::Pair).transformLatest { (clock, timeZone) ->
        collect(clock.clock, timeZone, maxTick, collector = ::emit)
    }
}

/**
 * Function that calls [collector] with the [TickingValue.value] calculated by this [TickingValueProvider.provide] each time there is a new
 * value.
 *
 * @param clock the [Clock] to use to obtain the [Instant] for the reference point
 * @param timeZone the [TimeZone] to use for the reference point
 * @param maxTick the maximum amount of to wait for a recomputation (as long as this underlying provider returns a non-null
 * [TickingValue.nextTick]). If null (the default), no max tick bound is applied.
 * @param initialReference optional parameter that allows to explicitly specify the initial reference. Useful if the initial value needs to
 * be available immediately outside of this function to avoid calculating it twice.
 * @param initialReference optional parameter that allows to explicitly specify the initial value. Useful if the initial value needs to
 * be available immediately outside of this function to avoid calculating it twice.
 * @param collector the function that gets called once immediately initially, and then once for every subsequent [TickingValue.value],
 * delayed by each [TickingValue.nextTick].
 */
@OptIn(ExperimentalCoroutinesApi::class)
public suspend fun <T> TickingValueProvider<T>.collect(
    clock: Clock,
    timeZone: TimeZone,
    maxTick: Duration? = null,
    initialReference: Zoned<Instant> = Zoned(clock.now(), timeZone),
    initialValue: TickingValue<T> = this.provide(initialReference),
    collector: suspend (T) -> Unit,
) {
    var lastReference = initialReference
    var last = initialValue

    while (last.nextTick != null) {
        collector(last.value)
        // There might have been a small amount of time between when the nextTick was calculated and now due to slowness of provide() or
        // scheduling delays of the coroutines.
        // For this reason, we compute this offset here to account for any time lost due to that, and subtract it from the delay time.
        val offset = clock.now() - lastReference.value
        delay(last.nextTick - offset)

        lastReference = Zoned(clock.now(), timeZone)
        last = this.provide(lastReference)
        if (last.nextTick != null) {
            // If the returned maxTick is null, it means that the localized string is not affected by the reference point anymore,
            // so it doesn't make sense to keep applying maxTick in this case
            last = last.withNextTickAtMost(maxTick)
        }
    }

    // When nextTick is null, while never runs, so we need to emit the very last value
    collector(last.value)
}
