package dev.mmauro.datetimepolyglot.styles

import js.intl.SecondFormat
import js.intl.numeric
import js.intl.twoDigit

/**
 * See [MSDN doc](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#second)
 */
internal fun SecondStyle.toSecondFormat(): SecondFormat = when (this) {
    SecondStyle.NUMERIC -> SecondFormat.numeric
    SecondStyle.NUMERIC_PADDED_2_DIGITS -> SecondFormat.twoDigit
}