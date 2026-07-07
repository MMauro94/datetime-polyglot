package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneOptions
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle.Generic
import kotlinx.datetime.TimeZone

/**
 * Localization options for [ZonedYearLocalizer].
 */
@ExperimentalZonedLocalizer
data class ZonedYearOptions(
    val yearOptions: YearOptions = YearOptions(),
    val timeZoneOptions: TimeZoneOptions = TimeZoneOptions(),
)

/**
 * Localizer for [Zoned] years ([Zoned]<[Int]>).
 *
 * This behaves identically to [YearLocalizer], but it also adds a [TimeZone] in the mix.
 *
 * Because a year represents a big span of time, the timezone could have different offsets at different moments within the year.
 * For this reason, only the [TimeZoneStyle.Generic] subset is allowed in the options for the time zone style.
 *
 * Because kotlinx-datetime doesn't provide a standard type for a year, there is no extension function equivalent for one-off localizations.
 *
 * Examples:
 * - `2026, Los Angeles Time`
 * - `26 AD, PT`
 * - `2026 Anno Domini, Pacific Time`
 */
@ExperimentalZonedLocalizer
class ZonedYearLocalizer(
    options: ZonedYearOptions = ZonedYearOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeZonedLocalizer<Int> {

    private val zonedLocalizer = ZonedLocalizer(
        locale = locale,
        datePartLocalizer = YearLocalizer(options.yearOptions, locale),
        timeZoneOptions = options.timeZoneOptions,
    )

    override fun localize(value: Zoned<Int>): String {
        return zonedLocalizer.localize(value)
    }
}