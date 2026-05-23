package dev.mmauro.datetimepolyglot.styles

import js.intl.HourFormat
import js.intl.numeric
import js.intl.twoDigit

/**
 * See [MSDN doc](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#hour)
 */
internal fun HourStyle.toHourFormat(): HourFormat = when (this) {
    HourStyle.NUMERIC -> HourFormat.numeric
    HourStyle.NUMERIC_PADDED_2_DIGITS -> HourFormat.twoDigit
}