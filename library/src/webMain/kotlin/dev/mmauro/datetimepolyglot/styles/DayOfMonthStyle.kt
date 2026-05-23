package dev.mmauro.datetimepolyglot.styles

import js.intl.DayFormat
import js.intl.numeric
import js.intl.twoDigit

/**
 * See [MSDN doc](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#day)
 */
internal fun DayOfMonthStyle.toDayFormat(): DayFormat = when (this) {
    DayOfMonthStyle.NUMERIC -> DayFormat.numeric
    DayOfMonthStyle.NUMERIC_PADDED_2_DIGITS -> DayFormat.twoDigit
}