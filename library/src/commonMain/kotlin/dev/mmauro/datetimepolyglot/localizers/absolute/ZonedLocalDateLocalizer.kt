package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneOptions
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.LocalDate

/**
 * Localization options for [ZonedLocalDateLocalizer].
 */
// For now this constructor is internal because it's useless for users to manually create a DateOptions instance
@ExperimentalZonedLocalizer
data class ZonedDateOptions internal constructor(
    val dateOptions: DateOptions,
    val timeZoneOptions: TimeZoneOptions = TimeZoneOptions(),
) {
    constructor(
        dateStyleOptions: DateStyleOptions,
        timeZoneOptions: TimeZoneOptions = TimeZoneOptions(),
    ) : this(DateOptions(dateStyleOptions), timeZoneOptions)
}

/**
 * Localizer for [Zoned]<[LocalDate]>.
 *
 * This behaves identically to [LocalDateLocalizer], but it also adds a [TimeZone] in the mix.
 *
 * Because a [LocalDate] represents a big span of time, the timezone could have different offsets at different moments within the day.
 * For this reason, only the [TimeZoneStyle.Generic] subset is allowed in the options for the time zone style.
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [Zoned.localize] for one-off localizations.
 *
 * Examples:
 * - `January 2026, Los Angeles Time`
 * - `Jan 26, PT`
 * - `01/2026, Pacific Time`
 */
@ExperimentalZonedLocalizer
class ZonedLocalDateLocalizer(
    options: ZonedDateOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeZonedLocalizer<LocalDate> {

    private val zonedLocalizer = ZonedLocalizer(
        locale = locale,
        datePartLocalizer = LocalDateLocalizer(options.dateOptions, locale),
        timeZoneOptions = options.timeZoneOptions,
    )

    override fun localize(value: Zoned<LocalDate>): String {
        return zonedLocalizer.localize(value)
    }
}

/**
 * Localizes this [LocalDate] with the given [options] in the given [locale].
 *
 * @see ZonedLocalDateLocalizer
 */
@ExperimentalZonedLocalizer
fun Zoned<LocalDate>.localize(
    options: ZonedDateOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = ZonedLocalDateLocalizer(options, locale).localize(this)