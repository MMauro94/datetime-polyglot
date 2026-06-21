package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import kotlinx.datetime.TimeZone

/**
 * Options for [TimeZoneLocalizer] and [TimeZone.localize]
 */
data class TimeZoneOptions(
    val style: TimeZoneStyle.Generic = TimeZoneStyle.Generic.LOCATION,
)

/**
 * Class to localize a standalone [TimeZone].
 */
expect class TimeZoneLocalizer(
    options: TimeZoneOptions = TimeZoneOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<TimeZone> {
    override fun localize(value: TimeZone): String
}

/**
 * Localizes this [TimeZone] for use in a standalone context.
 *
 * @see TimeZoneLocalizer
 */
fun TimeZone.localize(
    options: TimeZoneOptions = TimeZoneOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) = TimeZoneLocalizer(options, locale).localize(this)
