package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneOptions
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth

/**
 * Localization options for [ZonedYearMonthLocalizer].
 */
@ExperimentalZonedLocalizer
data class ZonedYearMonthOptions(
    val yearMonthOptions: YearMonthOptions,
    val timeZoneOptions: TimeZoneOptions = TimeZoneOptions(),
)

/**
 * Localizer for [Zoned]<[YearMonth]>.
 *
 * This behaves identically to [YearMonthLocalizer], but it also adds a [TimeZone] in the mix.
 *
 * Because a [YearMonth] represents a big span of time, the timezone could have different offsets at different moments within the year.
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
class ZonedYearMonthLocalizer(
    options: ZonedYearMonthOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeZonedLocalizer<YearMonth> {

    private val zonedLocalizer = ZonedLocalizer(
        locale = locale,
        datePartLocalizer = YearMonthLocalizer(options.yearMonthOptions, locale),
        timeZoneOptions = options.timeZoneOptions,
    )

    override fun localize(value: Zoned<YearMonth>): String {
        return zonedLocalizer.localize(value)
    }
}

/**
 * Localizes this [YearMonth] with the given [options] in the given [locale].
 *
 * @see ZonedYearMonthLocalizer
 */
@ExperimentalZonedLocalizer
fun Zoned<YearMonth>.localize(
    options: ZonedYearMonthOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = ZonedYearMonthLocalizer(options, locale).localize(this)