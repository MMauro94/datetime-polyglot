package dev.mmauro.datetimepolyglot.styles

import js.intl.MonthFormat
import js.intl.long
import js.intl.narrow
import js.intl.numeric
import js.intl.short
import js.intl.twoDigit

/**
 * See [MSDN doc](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#month)
 */
internal fun MonthStyle.toMonthFormat(): MonthFormat = when (this) {
    MonthStyle.NUMERIC -> MonthFormat.numeric
    MonthStyle.NUMERIC_PADDED_2_DIGITS -> MonthFormat.twoDigit
    MonthStyle.NARROW -> MonthFormat.narrow
    MonthStyle.ABBREVIATED -> MonthFormat.short
    MonthStyle.WIDE -> MonthFormat.long
}