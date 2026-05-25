package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import kotlinx.datetime.Month

/**
 * Options for [MonthLocalizer] and [Month.localize]
 */
data class MonthOptions(
    val style: MonthStyle = MonthStyle.WIDE,
)

/**
 * Class to localize a [Month].
 *
 * This class localizes for **standalone** usage of the string. For month names, this is typically the nominative grammatical form.
 *
 * For more info, see [Unicode page](https://www.unicode.org/reports/tr35/tr35-dates.html#months_days_quarters_eras).
 */
expect class MonthLocalizer(
    locale: PlatformLocale = getDefaultLocale(),
    options: MonthOptions = MonthOptions(),
) : DateTimeLocalizer<Month> {
    override fun localize(value: Month): String
}

/**
 * Localizes this [Month] for use in a standalone context.
 *
 * @see MonthLocalizer
 */
fun Month.localize(
    locale: PlatformLocale = getDefaultLocale(),
    options: MonthOptions = MonthOptions(),
) = MonthLocalizer(locale, options).localize(this)
