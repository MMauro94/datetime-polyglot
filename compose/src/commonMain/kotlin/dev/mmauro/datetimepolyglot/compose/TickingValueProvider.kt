package dev.mmauro.datetimepolyglot.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import dev.mmauro.datetimepolyglot.ClockWrapper
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.TickingValueProvider
import dev.mmauro.datetimepolyglot.Zoned
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * Collects and returns values from [TickingValueProvider] in a lifecycle-aware manner.
 *
 * @param lifecycleOwner [LifecycleOwner] whose `lifecycle` is used to restart collecting values from `this` [TickingValueProvider]
 * @param minActiveState [Lifecycle.State] in which the upstream [TickingValueProvider] gets collected. The collection will stop if the
 * lifecycle falls below that state, and will restart if it's in that state again.
 * @param clock the [Clock] to use for localization. By default, it collects [SYSTEM_CLOCK], so any clock change is immediately reflected
 * @param timeZone the [TimeZone] to use for localization. By default, it collects [SYSTEM_TIMEZONE], so any timezone change is immediately
 * reflected
 * @param maxTick the maximum amount of to wait for a recomputation (as long as this underlying provider returns a non-null
 * [TickingValue.nextTick]). If null (the default), no max tick bound is applied.
 */
@Composable
public fun <T> TickingValueProvider<T>.collectWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    clock: ClockWrapper = SYSTEM_CLOCK.collectAsStateWithLifecycle(lifecycleOwner, minActiveState).value,
    timeZone: TimeZone = SYSTEM_TIMEZONE.collectAsStateWithLifecycle(lifecycleOwner, minActiveState).value,
    maxTick: Duration? = null,
): T {
    var reference by remember(this, clock, timeZone) { mutableStateOf(Zoned(clock.clock.now(), timeZone)) }
    val tickingValue = remember(this, reference) { provide(reference) }

    LaunchedEffect(tickingValue, reference, clock, timeZone, maxTick) {
        lifecycleOwner.repeatOnLifecycle(minActiveState) {
            if (tickingValue.delay(reference, clock.clock, maxTick = maxTick)) {
                reference = Zoned(clock.clock.now(), timeZone)
            }
        }
    }
    return tickingValue.value
}
