package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.styles.DayOfMonthStyle
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import dev.mmauro.datetimepolyglot.styles.DayPeriodStyle
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle

internal object Defaults {

    val ERA: EraStyle? = null
    val YEAR = YearStyle.NUMERIC
    val MONTH = MonthStyle.WIDE
    val DAY_OF_MONTH = DayOfMonthStyle.NUMERIC
    val DAY_OF_WEEK: DayOfWeekStyle? = null
    val DAY_PERIOD: DayPeriodStyle? = null
    val DATE = DateStyle.LONG

    val JOINER = DateStyle.LONG

    val HOUR = HourStyle.NUMERIC
    val MINUTE = MinuteStyle.NUMERIC
    val SECOND: SecondStyle? = null
    const val FRACTIONAL_SECONDS = 0
    val HOUR_CYCLE: HourCycle? = null
    val LOCAL_TIME = LocalTimeStyle.SHORT
    // Needs to be lazy to avoid recursive init issues in TimeOptions.kt
    val ZONED_TIME by lazy { ZonedTimeComponents() }

    val TIME_ZONE = TimeZoneStyle.Generic.LOCATION
}