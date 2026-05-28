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
    val eraStyle: EraStyle? = null,
    val yearStyle: YearStyle = YearStyle.NUMERIC_PADDED_4_DIGITS,
    val monthStyle: MonthStyle,
    val dayOfMonthStyle: DayOfMonthStyle? = null,
    val dayOfWeekStyle: DayOfWeekStyle? = null,
) : DateOptions {
    init {
        require(dayOfWeekStyle != null || dayOfMonthStyle != null) {
            "At least one between day of week and day of month styles need to be not null"
        }
    }
}