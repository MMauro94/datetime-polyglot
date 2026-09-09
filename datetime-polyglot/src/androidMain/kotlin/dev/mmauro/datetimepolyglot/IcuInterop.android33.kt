package dev.mmauro.datetimepolyglot

import android.icu.text.DateFormat
import android.icu.text.DateTimePatternGenerator
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal fun DateTimePatternGenerator.getDefaultHourCycleAndroid33(): HourCycle {
    return when (defaultHourCycle) {
        DateFormat.HourCycle.HOUR_CYCLE_11 -> HourCycle.HOURS_11
        DateFormat.HourCycle.HOUR_CYCLE_12 -> HourCycle.HOURS_12
        DateFormat.HourCycle.HOUR_CYCLE_23 -> HourCycle.HOURS_23
        DateFormat.HourCycle.HOUR_CYCLE_24 -> HourCycle.HOURS_24
    }
}
