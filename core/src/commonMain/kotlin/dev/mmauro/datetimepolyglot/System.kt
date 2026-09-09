package dev.mmauro.datetimepolyglot

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

/**
 * Returns a [Flow] that emits [ClockWrapper] of [Clock.System] every time the system clock updates.
 * Initially, a value is always emitted a first time, and it later waits for changes.
 *
 * Note: this emits [ClockWrapper] instead of plain [Clock.System] because the latter is a singleton, and [StateFlow] always conflates
 * identical values.
 *
 * Note: currently only works for Android. In the rest of the platforms, the flow returns only the initial value and does not subscribe to
 * clock change events.
 */
public expect val SYSTEM_CLOCK: StateFlow<ClockWrapper>

/**
 * Returns a [Flow] that emits the current system [TimeZone] every time it changes.
 * Initially, the current system [TimeZone] is always emitted a first time.
 *
 * Note: currently only works for Android. In the rest of the platforms, the flow returns only the initial value and does not subscribe to
 * time zone change events.
 */
public expect val SYSTEM_TIMEZONE: StateFlow<TimeZone>

/**
 * [StateFlow] that returns the current timezone at the moment of subscription.
 *
 * Every time the value is [collect]ed, the current timezone is refreshed and all subscribers will get the new value (if it has changed).
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
internal val STATIC_TIMEZONE = object : StateFlow<TimeZone> {

    val proxy = MutableStateFlow(TimeZone.currentSystemDefault())

    override val value get() = proxy.value
    override val replayCache get() = proxy.replayCache

    override suspend fun collect(collector: FlowCollector<TimeZone>): Nothing {
        proxy.value = TimeZone.currentSystemDefault()
        proxy.collect(collector)
    }
}
