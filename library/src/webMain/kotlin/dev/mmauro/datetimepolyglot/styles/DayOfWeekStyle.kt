package dev.mmauro.datetimepolyglot.styles

import js.intl.WeekdayFormat
import js.intl.long
import js.intl.narrow
import js.intl.short

/**
 * See [MSDN doc](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#weekday)
 */
internal fun DayOfWeekStyle.toWeekdayFormat(): WeekdayFormat = when (this) {
    DayOfWeekStyle.NARROW -> WeekdayFormat.narrow
    // There is no support for two-letters, fall back on three
    DayOfWeekStyle.SHORT, DayOfWeekStyle.ABBREVIATED -> WeekdayFormat.short
    DayOfWeekStyle.WIDE -> WeekdayFormat.long
}
