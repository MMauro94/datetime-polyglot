package dev.mmauro.datetimepolyglot.compose.localizers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mmauro.datetimepolyglot.ClockWrapper
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.TickingValueProvider
import dev.mmauro.datetimepolyglot.compose.collectWithLifecycle
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.toTickingValueProvider
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * Utility function that collects the values from this [LOCALIZER] in a lifecycle-aware manner.
 *
 * @param value the value to localize
 * @param clock the [Clock] to use for localization. By default, it collects [SYSTEM_CLOCK], so any clock change is immediately reflected
 * @param timeZone the [TimeZone] to use for localization. By default, it collects [SYSTEM_TIMEZONE], so any timezone change is immediately
 * reflected
 * @param maxTick the maximum amount of to wait for a recomputation (as long as this underlying provider returns a non-null
 * [TickingValue.nextTick]). If null (the default), no max tick bound is applied.
 *
 * @see TickingValueProvider.collectWithLifecycle
 */
@Composable
public fun <T, R, LOCALIZER : PolyglotReferenceValueLocalizer<T, R>> LOCALIZER.localize(
    value: T,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    clock: ClockWrapper = SYSTEM_CLOCK.collectAsStateWithLifecycle(lifecycleOwner, minActiveState).value,
    timeZone: TimeZone = SYSTEM_TIMEZONE.collectAsStateWithLifecycle(lifecycleOwner, minActiveState).value,
    maxTick: Duration? = null,
): R {
    val provider = remember(this, value) { toTickingValueProvider(value) }
    return provider.collectWithLifecycle(
        lifecycleOwner = lifecycleOwner,
        minActiveState = minActiveState,
        clock = clock,
        timeZone = timeZone,
        maxTick = maxTick,
    )
}
