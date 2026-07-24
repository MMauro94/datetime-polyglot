package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import kotlinx.datetime.TimeZone

/**
 * Options for [TimeZoneLocalizer] and [TimeZone.localize]
 */
data class TimeZoneOptions(
    val style: TimeZoneStyle.Generic = TimeZoneStyle.Generic.LOCATION,
) : PolyglotLocalizerOptions<TimeZoneLocalizer> {
    override fun localizer(locale: PlatformLocale) = TimeZoneLocalizer(this, locale)
}

/**
 * Class to localize a standalone [TimeZone] for standalone usage.
 *
 * Please note that is class is intended to localize a time zone in a way that is agnostic to the actual offset of the timezone in a given
 * moment, as this value can change (e.g. because of DST).
 *
 * For this reason, only the [TimeZoneStyle.Generic] subset is allowed in the options.
 *
 * Examples:
 * - `America/Los_Angeles`
 * - `PT`
 * - `Pacific Time`
 * - `Los Angeles Time`
 */
expect class TimeZoneLocalizer(
    options: TimeZoneOptions = TimeZoneOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeLocalizer<TimeZone> {
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
