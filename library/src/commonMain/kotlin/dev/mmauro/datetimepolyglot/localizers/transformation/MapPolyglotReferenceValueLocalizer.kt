package dev.mmauro.datetimepolyglot.localizers.transformation

import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.flatMap
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import kotlin.time.Instant

internal class MapPolyglotReferenceValueLocalizer<T, R1, R2>(
    val localizer: PolyglotReferenceValueLocalizer<T, R1>,
    val map: (value: T, localized: TickingValue<R1>, reference: Zoned<Instant>) -> TickingValue<R2>,
) : PolyglotReferenceValueLocalizer<T, R2> {

    override fun localize(value: T, reference: Zoned<Instant>): TickingValue<R2> {
        val localized = localizer.localize(value, reference)
        return localized.flatMap { map(value, localized, reference) }
    }
}

/**
 * Returns a new [PolyglotReferenceValueLocalizer] that localizes the received value using this localizer, calls [map], and returns whatever
 * [TickingValue] the mapping function returns.
 *
 * @param map mapping function. Receives as input:
 *  - value: the value being localized of type [T]
 *  - localized: the localized output of this localizer
 *  - reference: the reference point
 */
public fun <T, R1, R2> PolyglotReferenceValueLocalizer<T, R1>.map(
    map: (value: T, localized: TickingValue<R1>, reference: Zoned<Instant>) -> TickingValue<R2>,
): PolyglotReferenceValueLocalizer<T, R2> {
    return MapPolyglotReferenceValueLocalizer(this, map)
}
