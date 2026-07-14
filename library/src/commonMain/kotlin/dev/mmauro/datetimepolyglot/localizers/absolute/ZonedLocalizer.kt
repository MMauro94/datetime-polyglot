package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.ExperimentalZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneLocalizer
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneOptions
import dev.mmauro.datetimepolyglot.utils.joinDateAndTime

@ExperimentalZonedLocalizer
internal class ZonedLocalizer<T>(
    private val locale: PlatformLocale,
    private val datePartLocalizer: PolyglotDateTimeLocalizer<T>,
    timeZoneOptions: TimeZoneOptions,
) : PolyglotDateTimeZonedLocalizer<T> {

    private val timeZoneLocalizer = TimeZoneLocalizer(timeZoneOptions, locale)

    override fun localize(value: Zoned<T>): String {
        return joinDateAndTime(
            locale = locale,
            style = DateStyle.SHORT,
            date = datePartLocalizer.localize(value.value),
            time = timeZoneLocalizer.localize(value.timeZone)
        )
    }
}