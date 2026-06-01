package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.styles.DayOfMonthStyle
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle

sealed interface DateOptions

enum class DateStyle : DateOptions {
    SHORT,
    MEDIUM,
    LONG,
    FULL,
}

data class DateComponents(
    override val eraStyle: EraStyle? = null,
    override val yearStyle: YearStyle = YearStyle.NUMERIC_PADDED_4_DIGITS,
    override val monthStyle: MonthStyle,
    override val dayOfMonthStyle: DayOfMonthStyle? = null,
    override val dayOfWeekStyle: DayOfWeekStyle? = null,
) : DateOptions, ComponentsOptions.Date.Components {
    init {
        require(dayOfWeekStyle != null || dayOfMonthStyle != null) {
            "At least one between day of week and day of month styles need to be not null"
        }
    }
}