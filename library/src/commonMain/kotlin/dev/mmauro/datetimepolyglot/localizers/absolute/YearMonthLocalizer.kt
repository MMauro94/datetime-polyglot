package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import kotlinx.datetime.YearMonth

data class YearMonthOptions(
    override val eraStyle: EraStyle? = null,
    override val yearStyle: YearStyle = YearStyle.NUMERIC,
    override val monthStyle: MonthStyle?,
) : ComponentsOptions.Date.Components {
    override val dayOfMonthStyle: Nothing? get() = null
    override val dayOfWeekStyle: Nothing? get() = null
}

expect class YearMonthLocalizer(
    options: YearMonthOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<YearMonth> {

    override fun localize(value: YearMonth): String
}

/**
 * Localizes this [YearMonth] with the given [options] in the given [locale].
 *
 * @see LocalTimeLocalizer
 */
fun YearMonth.localize(
    options: YearMonthOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = YearMonthLocalizer(options, locale).localize(this)

