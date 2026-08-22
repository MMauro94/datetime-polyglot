package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import kotlinx.datetime.YearMonth

/**
 * Localization options for [YearMonthLocalizer] and [YearMonth.localize].
 */
data class YearMonthOptions(
    override val eraStyle: EraStyle? = null,
    override val yearStyle: YearStyle = YearStyle.NUMERIC,
    override val monthStyle: MonthStyle?,
) : ComponentsOptions.Date.Components, PolyglotLocalizerOptions<YearMonthLocalizer> {
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Implementation detail, shouldn't be used")
    override val dayOfMonthStyle: Nothing? get() = null
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Implementation detail, shouldn't be used")
    override val dayOfWeekStyle: Nothing? get() = null

    override fun localizer(locale: PlatformLocale) = YearMonthLocalizer(this, locale)
}

/**
 * Localizer for [YearMonth].
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [YearMonth.localize] for one-off localizations.
 *
 * Examples:
 * - `January 2026`
 * - `Jan 26`
 * - `01/2026`
 */
expect class YearMonthLocalizer(
    options: YearMonthOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeLocalizer<YearMonth> {

    override fun localize(value: YearMonth): String
}

/**
 * Localizes this [YearMonth] with the given [options] in the given [locale].
 *
 * @see YearMonthLocalizer
 */
fun YearMonth.localize(
    options: YearMonthOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = YearMonthLocalizer(options, locale).localize(this)

