package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.formatNumeric
import dev.mmauro.datetimepolyglot.getRelativeDateTimeFormatter
import dev.mmauro.datetimepolyglot.localizers.PolyglotValueLocalizer
import kotlin.time.Duration

actual class RelativeDurationLocalizer actual constructor(
    private val options: RelativeDurationOptions,
    private val locale: PlatformLocale
) : PolyglotValueLocalizer<Duration, TickingValue<String>> {

    private val relativeDateTimeFormatter = getRelativeDateTimeFormatter(locale, options.style)

    actual override fun localize(value: Duration): TickingValue<String> {
        return value.internalLocalize(options, locale) { value, unit ->
            relativeDateTimeFormatter.formatNumeric(value, unit)
        }
    }
}