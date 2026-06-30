package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.zonedNow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * A generic [PolyglotLocalizer] that accepts a value of type [T] and a [Zoned]<[Instant]> reference point, and returns a [TickingValue] of
 * type [String].
 *
 * The [TickingValue.nextTick] represent the amount of time that the reference needs to advance by for the [TickingValue.value] to change,
 * thus requiring a new call to [localize] with the same exact value but updated reference.
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
 * the underlying [TickingValue.nextTick].
 *
 * The flow will automatically complete as soon as a localization returns `null` as [TickingValue.nextTick], as that means that the
 * localization is now "stable" and will not require further passes.
 */
fun <T> PolyglotReferenceValueLocalizer<T>.localizeAsFlow(value: T, clock: Clock = Clock.System): Flow<String> {
    return flow {
        var last: TickingValue<String>

        do {
            last = localize(value, clock)
            emit(last.value)

            if (last.nextTick != null) {
                delay(last.nextTick)
            }
        } while (last.nextTick != null)
    }
}