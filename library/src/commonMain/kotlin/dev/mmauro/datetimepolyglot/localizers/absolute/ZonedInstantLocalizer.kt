package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalDateTimeOptions
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/**
 * Localization options for [ZonedInstantLocalizer] and [Zoned.localize] (where [Zoned] is of type [Instant]).
 */
data class ZonedInstantOptions private constructor(
    val dateOptions: DateOptions,
    val timeOptions: ZonedTimeOptions,
) {

    constructor(dateOptions: DateStyleOptions, timeOptions: ZonedTimeOptions) : this(DateOptions(dateOptions), timeOptions)
    constructor(dateOptions: DateStyleOptions, timeOptions: TimeStyleOptions.Zoned) : this(dateOptions, ZonedTimeOptions(timeOptions))
}

/**
 * Localizer for an [Instant] paired with a [TimeZone] (see [Zoned]).
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [Zoned.localize] for one-off localizations.
 *
 * Note that the same warning in [LocalTimeLocalizer] for "gaps" in the time components applies here.
 *
 * Examples:
 * - `1/8/26 9:05 PM PST`
 * - `Jan 8, 2026, 9 at night Pacific Daylight Time`
 * - `January 8, 2026 at 9:31:45 PM GMT-07:00`
 * - `Thursday, January 8, 2026 at 21:05 Los Angeles Time`
 */
expect class ZonedInstantLocalizer(
    options: ZonedInstantOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeZonedLocalizer<Instant> {

    override fun localize(value: Zoned<Instant>): String
}

/**
 * Localizes this [Instant] at this [TimeZone] with the given [options] in the given [locale].
 *
 * @see ZonedInstantLocalizer
 */
fun Zoned<Instant>.localize(
    options: ZonedInstantOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = ZonedInstantLocalizer(options, locale).localize(this)

