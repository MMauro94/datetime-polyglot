package dev.mmauro.datetimepolyglot.localizers.transformation

import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.flatMap
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import kotlin.time.Instant

internal class MapPolyglotReferenceValueLocalizer<T>(
    val localizer: PolyglotReferenceValueLocalizer<T>,
    val map: (value: T, localized: TickingValue<String>, reference: Zoned<Instant>) -> TickingValue<String>
) : PolyglotReferenceValueLocalizer<T> {

    override fun localize(value: T, reference: Zoned<Instant>): TickingValue<String> {
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
public fun <T> PolyglotReferenceValueLocalizer<T>.map(
    map: (value: T, localized: TickingValue<String>, reference: Zoned<Instant>) -> TickingValue<String>
): PolyglotReferenceValueLocalizer<T> {
    return MapPolyglotReferenceValueLocalizer(this, map)
}