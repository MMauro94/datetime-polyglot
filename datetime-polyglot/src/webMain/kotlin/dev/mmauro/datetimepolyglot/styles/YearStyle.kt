package dev.mmauro.datetimepolyglot.styles

import js.intl.YearFormat
import js.intl.numeric
import js.intl.twoDigit

/**
 * See [MSDN doc](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#year)
 */
internal fun YearStyle.toYearFormat(): YearFormat = when (this) {
    // There is no support for padded, fall back on numeric
    YearStyle.NUMERIC, YearStyle.NUMERIC_PADDED_4_DIGITS -> YearFormat.numeric
    YearStyle.NUMERIC_2_DIGITS -> YearFormat.twoDigit
}
