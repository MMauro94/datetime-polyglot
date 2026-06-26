package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.Measure
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getMeasureFormat
import dev.mmauro.datetimepolyglot.utils.toIcuTimeUnit
import kotlin.time.Duration

actual class DurationLocalizer actual constructor(
    private val options: DurationOptions,
    locale: PlatformLocale
) : DateTimeLocalizer<Duration> {

    private val measureFormat = getMeasureFormat(locale, options.style)

    actual override fun localize(value: Duration): String {
        value.requireValidAbsoluteDuration()

        val measures = options
            .detectUnits(value)
            .filter(options)
            .map { (value, unit) -> Measure(value, unit.toIcuTimeUnit()) }

        return measureFormat.format(measures)
    }
}