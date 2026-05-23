package dev.mmauro.datetimepolyglot

import com.ibm.icu.text.DateFormat
import com.ibm.icu.text.DateTimePatternGenerator

internal actual fun PlatformLocale.getDefaultHourCycle(): HourCycle {
    return when (DateTimePatternGenerator.getInstance(this).defaultHourCycle) {
        DateFormat.HourCycle.HOUR_CYCLE_11 -> HourCycle.HOURS_11
        DateFormat.HourCycle.HOUR_CYCLE_12 -> HourCycle.HOURS_12
        DateFormat.HourCycle.HOUR_CYCLE_23 -> HourCycle.HOURS_23
        DateFormat.HourCycle.HOUR_CYCLE_24 -> HourCycle.HOURS_24
    }
}