package dev.mmauro.datetimepolyglot

import android.icu.text.DateFormat
import android.icu.text.DateTimePatternGenerator
import android.icu.text.SimpleDateFormat

internal actual typealias SimpleDateFormat = SimpleDateFormat

internal actual typealias DateFormat = DateFormat

internal actual fun getDateFormatForSkeleton(skeleton: String, locale: PlatformLocale) =
    DateFormat.getInstanceForSkeleton(skeleton, locale)

internal actual fun PlatformLocale.getDefaultHourCycle(): HourCycle {
    return when (DateTimePatternGenerator.getInstance(this).defaultHourCycle) {
        DateFormat.HourCycle.HOUR_CYCLE_11 -> HourCycle.HOURS_11
        DateFormat.HourCycle.HOUR_CYCLE_12 -> HourCycle.HOURS_12
        DateFormat.HourCycle.HOUR_CYCLE_23 -> HourCycle.HOURS_23
        DateFormat.HourCycle.HOUR_CYCLE_24 -> HourCycle.HOURS_24
    }
}