package dev.mmauro.datetimepolyglot.styles

import js.intl.MinuteFormat
import js.intl.numeric
import js.intl.twoDigit

/**
 * See [MSDN doc](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#minute)
 */
internal fun MinuteStyle.toMinuteFormat(): MinuteFormat = when (this) {
    MinuteStyle.NUMERIC -> MinuteFormat.numeric
    MinuteStyle.NUMERIC_PADDED_2_DIGITS -> MinuteFormat.twoDigit
}