package dev.mmauro.datetimepolyglot.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mmauro.datetimepolyglot.ClockWrapper
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.TickingValueProvider
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.collect
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * Collects values from [TickingValueProvider] and represents its latest value via [State] in a lifecycle-aware manner.
 *
 * @param clock the [Clock] to use for localization. By default, it collects [SYSTEM_CLOCK], so any clock change is immediately reflected
 * @param timeZone the [TimeZone] to use for localization. By default, it collects [SYSTEM_TIMEZONE], so any timezone change is immediately
 * reflected
 * @param maxTick the maximum amount of to wait for a recomputation (as long as this underlying provider returns a non-null
 * [TickingValue.nextTick]). If null (the default), no max tick bound is applied.
 *
 * @see Flow.collectAsStateWithLifecycle
 * @see TickingValueProvider.collect
 */
@Composable
public fun <T> TickingValueProvider<T>.collectAsStateWithLifecycle(
    clock: ClockWrapper = SYSTEM_CLOCK.collectAsStateWithLifecycle().value,
    timeZone: TimeZone = SYSTEM_TIMEZONE.collectAsStateWithLifecycle().value,
    maxTick: Duration? = null,
): State<T> {
    val initialReference = remember(clock, timeZone) { Zoned(clock.clock.now(), timeZone) }
    val initialValue = remember(this, initialReference) { provide(initialReference) }

    val mutState = remember { mutableStateOf(initialValue.value) }

    // To avoid having to wait 1 frame for the LaunchedEffect to update to the newest value, we force the mutable state update with a
    // SideEffect here
    SideEffect(initialValue) {
        mutState.value = initialValue.value
    }

    LaunchedEffect(this, clock, timeZone, maxTick, initialReference, initialValue) {
        collect(
            clock = clock.clock,
            timeZone = timeZone,
            maxTick = maxTick,
            initialReference = initialReference,
            initialValue = initialValue,
        ) {
            mutState.value = it
        }
    }
    return mutState
}
