package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

private val MONDAY_DATE = LocalDate(1970, 1, 5)

/**
 * Options for [DayOfWeekLocalizer] and [DayOfWeek.localize]
 */
data class DayOfWeekOptions(
    val style: DayOfWeekStyle = DayOfWeekStyle.WIDE,
) : PolyglotLocalizerOptions<DayOfWeekLocalizer> {
    override fun localizer(locale: PlatformLocale) = DayOfWeekLocalizer(this, locale)
}

/**
 * Class to localize a [DayOfWeek].
 *
 * This class localizes for **standalone** usage of the string.
 *
 * For more info, see [Unicode page](https://www.unicode.org/reports/tr35/tr35-dates.html#months_days_quarters_eras).
 *
 * Examples:
 * - `Monday`
 * - `Mon`
 * - `Mo`
 * - `M`
 */
expect class DayOfWeekLocalizer(
    options: DayOfWeekOptions = DayOfWeekOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeLocalizer<DayOfWeek> {
    override fun localize(value: DayOfWeek): String
}

/**
 * Localizes this [DayOfWeek] for use in a standalone context.
 *
 * @see DayOfWeekLocalizer
 */
fun DayOfWeek.localize(
    options: DayOfWeekOptions = DayOfWeekOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) = DayOfWeekLocalizer(options, locale).localize(this)

internal fun DayOfWeek.toArbitraryLocalDate() = MONDAY_DATE + DatePeriod(days = this.ordinal)
