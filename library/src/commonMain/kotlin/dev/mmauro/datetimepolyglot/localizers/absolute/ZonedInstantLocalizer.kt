package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/**
 * Localization options for [ZonedInstantLocalizer] and [Zoned.localize] (where [Zoned] is of type [Instant]).
 */
data class ZonedInstantOptions internal constructor(
    val dateOptions: DateOptions,
    val timeOptions: ZonedTimeOptions,
) {

    // On JS, it's forbidden to mix styles and components, even when using e.g. date style and time components or vice versa
    // So for now we are disallowing creation of mix-match DateTimeOptions
    // This can be enabled on a per-platform version by adding a fake invoke constructor in the companion object

    // With styles
    constructor(dateOptions: DateStyle, timeOptions: TimeOptions<TimeStyle.Zoned>) : this(DateOptions(dateOptions), timeOptions)
    constructor(dateOptions: DateStyle, timeOptions: TimeStyle.Zoned) : this(DateOptions(dateOptions), TimeOptions(timeOptions))

    // With components
    constructor(dateOptions: DateComponents, timeOptions: TimeOptions<TimeComponents.Zoned>) : this(DateOptions(dateOptions), timeOptions)
    constructor(dateOptions: DateComponents, timeOptions: TimeComponents.Zoned) : this(DateOptions(dateOptions), TimeOptions(timeOptions))

    companion object

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

