package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.ExperimentalZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneLocalizer
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneOptions
import dev.mmauro.datetimepolyglot.map
import dev.mmauro.datetimepolyglot.utils.joinDateAndTime
import kotlin.time.Instant

@ExperimentalZonedLocalizer
internal class RelativeZonedLocalizer<T>(
    private val locale: PlatformLocale,
    private val datePartLocalizer: PolyglotReferenceValueLocalizer<T>,
    timeZoneOptions: TimeZoneOptions,
) : PolyglotReferenceValueLocalizer<Zoned<T>> {

    private val timeZoneLocalizer = TimeZoneLocalizer(timeZoneOptions, locale)

    override fun localize(value: Zoned<T>, reference: Zoned<Instant>): TickingValue<String> {
        return datePartLocalizer.localize(value.value, reference).map {
            joinDateAndTime(
                locale = locale,
                style = DateStyle.SHORT,
                date = it,
                time = timeZoneLocalizer.localize(value.timeZone),
            )
        }
    }
}
