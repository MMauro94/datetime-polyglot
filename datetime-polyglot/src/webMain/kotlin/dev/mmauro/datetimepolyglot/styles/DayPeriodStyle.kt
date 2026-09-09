package dev.mmauro.datetimepolyglot.styles

import js.intl.DayPeriod
import js.intl.long
import js.intl.narrow
import js.intl.short

/**
 * See [MSDN doc](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#dayperiod)
 */
internal fun DayPeriodStyle.toDayPeriodFormat(): DayPeriod = when (this) {
    DayPeriodStyle.FLEXIBLE_NARROW -> DayPeriod.narrow
    DayPeriodStyle.FLEXIBLE_ABBREVIATED -> DayPeriod.short
    DayPeriodStyle.FLEXIBLE_WIDE -> DayPeriod.long
}
