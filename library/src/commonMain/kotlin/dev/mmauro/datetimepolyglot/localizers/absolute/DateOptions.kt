package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.styles.DayOfMonthStyle
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import dev.mmauro.datetimepolyglot.utils.joinDateAndTime

/**
 * Simple container class for [DateStyleOptions].
 *
 * This class is currently "useless", but exists for symmetry with [TimeOptions] and forward-compatibility in case we need to add global
 * options independent of usage of style or components options.
 */
public data class DateOptions(
    val styleOptions: DateStyleOptions = Defaults.DATE,
) : PolyglotLocalizerOptions<LocalDateLocalizer> {
    override fun localizer(locale: PlatformLocale): LocalDateLocalizer = LocalDateLocalizer(this, locale)
}

/**
 * Defines the localization style for a date (year, month and day).
 *
 * @see DateStyle
 * @see DateComponents
 */
public sealed interface DateStyleOptions : PolyglotLocalizerOptions<LocalDateLocalizer> {
    override fun localizer(locale: PlatformLocale): LocalDateLocalizer = LocalDateLocalizer(this, locale)
}

/**
 * Defines a preset date style for localizing a date (year, month and day).
 */
public enum class DateStyle : DateStyleOptions {
    SHORT,
    MEDIUM,
    LONG,
    FULL,
    ;

    public companion object {
        /**
         * Selects the [DateStyle] to use for the joiner pattern between date and time from the [options] used to localize the date part.
         *
         * @see joinDateAndTime
         */
        public fun detectDateTimeJoinerStyle(options: DateStyleOptions): DateStyle {
            return when (options) {
                is DateStyle -> options
                is DateComponents -> when (options.monthStyle) {
                    MonthStyle.WIDE -> if (options.dayOfWeekStyle !== null) FULL else LONG
                    MonthStyle.ABBREVIATED -> MEDIUM
                    else -> MEDIUM
                }
            }

        }
    }
}

/**
 * Class defining the style for each component of a date (year, month and day).
 */
public data class DateComponents(
    override val eraStyle: EraStyle? = Defaults.ERA,
    override val yearStyle: YearStyle = Defaults.YEAR,
    override val monthStyle: MonthStyle = Defaults.MONTH,
    override val dayOfMonthStyle: DayOfMonthStyle = Defaults.DAY_OF_MONTH,
    override val dayOfWeekStyle: DayOfWeekStyle? = Defaults.DAY_OF_WEEK,
) : DateStyleOptions, ComponentsOptions.Date.Components

internal fun DateOptions.toComponentOptions(): ComponentsOptions.Date {
    return when (styleOptions) {
        is DateStyle -> ComponentsOptions.Date.Style(styleOptions)
        is DateComponents -> styleOptions
    }
}