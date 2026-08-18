package dev.mmauro.datetimepolyglot

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

/**
 * Returns a [Flow] that emits [Clock.System] every time the system clock updates.
 * Initially, the [Clock.System] is always emitted a first time.
 *
 * Note: [Clock.System] is a singleton, so the [Flow] will always emit the same value. Nonetheless, a new emission means that the clock's
 * time has changed.
 *
 * Note: currently only works for Android. In the rest of the platforms, the flow returns only the initial value and does not subscribe to
 * clock change events.
 */
expect val SYSTEM_CLOCK: Flow<Clock.System>

/**
 * Returns a [Flow] that emits the current system [TimeZone] every time it changes.
 * Initially, the current system [TimeZone] is always emitted a first time.
 *
 * Note: currently only works for Android. In the rest of the platforms, the flow returns only the initial value and does not subscribe to
 * time zone change events.
 */
expect val SYSTEM_TIMEZONE: Flow<TimeZone>
