package dev.mmauro.datetimepolyglot

import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Clock

/**
 * This class simply wraps a plain [Clock].
 *
 * All it does is provide an identity to [Clock]: this is needed because [StateFlow] values are always conflated, but each time the clock
 * changes we emit an identical value of [Clock.System].
 */
public class ClockWrapper(public val clock: Clock)
