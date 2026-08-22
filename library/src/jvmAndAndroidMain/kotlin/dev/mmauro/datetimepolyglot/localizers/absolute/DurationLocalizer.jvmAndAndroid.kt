package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.Measure
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getMeasureFormat
import dev.mmauro.datetimepolyglot.localizers.PolyglotValueLocalizer
import dev.mmauro.datetimepolyglot.utils.toIcuTimeUnit
import kotlin.math.absoluteValue
import kotlin.time.Duration

public actual class DurationLocalizer actual constructor(
    internal actual val options: DurationOptions,
    private val locale: PlatformLocale
) : PolyglotValueLocalizer<Duration, String> {

    private val measureFormat = getMeasureFormat(locale, options.style)

    actual override fun localize(value: Duration): String {
        return value.internalLocalize(options, locale) { filteredUnits ->
            val measures = filteredUnits
                .mapIndexed { i, (value, unit) -> Measure(if (i == 0) value else value.absoluteValue, unit.toIcuTimeUnit()) }

            measureFormat.formatMeasures(*measures.toTypedArray())
        }
    }
}