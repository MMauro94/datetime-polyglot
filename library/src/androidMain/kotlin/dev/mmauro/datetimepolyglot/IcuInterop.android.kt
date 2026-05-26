package dev.mmauro.datetimepolyglot

import android.icu.text.DateFormat as AndroidDateFormat
import android.icu.text.DateTimePatternGenerator
import android.icu.util.TimeZone
import android.os.Build
import android.icu.text.SimpleDateFormat as AndroidSimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.Temporal

// SIMPLE DATE FORMAT
internal actual typealias SimpleDateFormat = AndroidSimpleDateFormat

internal actual fun SimpleDateFormat.format(localDate: LocalDate): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(localDate as Temporal)
    } else {
        timeZone = TimeZone.GMT_ZONE
        format(localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
    }
}

internal actual fun SimpleDateFormat.format(month: Month): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(month as Any)
    } else {
        timeZone = TimeZone.GMT_ZONE
        format(LocalDate.of(0, month, 1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
    }
}

// DATE FORMAT
internal actual typealias DateFormat = AndroidDateFormat

internal actual fun DateFormat.format(zonedDateTime: ZonedDateTime): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(zonedDateTime as Temporal)
    } else {
        timeZone = TimeZone.getTimeZone(zonedDateTime.zone.id)
        format(zonedDateTime.toInstant().toEpochMilli())
    }
}

internal actual fun getDateFormatForSkeleton(skeleton: String, locale: PlatformLocale) =
    DateFormat.getInstanceForSkeleton(skeleton, locale)

// LOCALE
internal actual fun PlatformLocale.getDefaultHourCycle(): HourCycle {
    return when (DateTimePatternGenerator.getInstance(this).defaultHourCycle) {
        AndroidDateFormat.HourCycle.HOUR_CYCLE_11 -> HourCycle.HOURS_11
        AndroidDateFormat.HourCycle.HOUR_CYCLE_12 -> HourCycle.HOURS_12
        AndroidDateFormat.HourCycle.HOUR_CYCLE_23 -> HourCycle.HOURS_23
        AndroidDateFormat.HourCycle.HOUR_CYCLE_24 -> HourCycle.HOURS_24
    }
}