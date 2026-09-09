package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import kotlinx.datetime.Month

/**
 * Options for [MonthLocalizer] and [Month.localize]
 */
public data class MonthOptions(
    val style: MonthStyle = MonthStyle.WIDE,
) : PolyglotLocalizerOptions<MonthLocalizer> {
    override fun localizer(locale: PlatformLocale): MonthLocalizer = MonthLocalizer(this, locale)
}

/**
 * Class to localize a [Month].
 *
 * This class localizes for **standalone** usage of the string. For month names, this is typically the nominative grammatical form.
 *
 * For more info, see [Unicode page](https://www.unicode.org/reports/tr35/tr35-dates.html#months_days_quarters_eras).
 *
 * Examples:
 * - `January`
 * - `Jan`
 * - `J`
 * - `1`
 */
public expect class MonthLocalizer(
    options: MonthOptions = MonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeLocalizer<Month> {
    override fun localize(value: Month): String
}

/**
 * Localizes this [Month] for use in a standalone context.
 *
 * @see MonthLocalizer
 */
public fun Month.localize(
    options: MonthOptions = MonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): String = MonthLocalizer(options, locale).localize(this)
