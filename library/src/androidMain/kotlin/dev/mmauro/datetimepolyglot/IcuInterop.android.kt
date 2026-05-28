package dev.mmauro.datetimepolyglot

import android.icu.text.DateFormat as AndroidDateFormat
import android.icu.text.DateTimePatternGenerator
import android.icu.util.TimeZone
import android.icu.util.ULocale
import android.os.Build
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeStyle
import android.icu.text.SimpleDateFormat as AndroidSimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.Temporal

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

internal actual fun DateFormat.format(localDate: LocalDate): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(localDate as Temporal)
    } else {
        timeZone = TimeZone.GMT_ZONE
        format(localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
    }
}

internal actual fun DateFormat.format(month: Month): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(month as Any)
    } else {
        timeZone = TimeZone.GMT_ZONE
        format(LocalDate.of(0, month, 1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
    }
}

// SIMPLE DATE FORMAT
internal actual typealias SimpleDateFormat = AndroidSimpleDateFormat

internal actual fun getDateFormatForSkeleton(skeleton: String, locale: PlatformLocale) =
    DateFormat.getInstanceForSkeleton(skeleton, locale) as SimpleDateFormat

internal actual fun getDateFormatInstance(dateStyle: DateStyle, locale: PlatformLocale) =
    AndroidDateFormat.getDateInstance(dateStyle.toDateFormatStyle(), locale) as SimpleDateFormat

internal actual fun getTimeFormatInstance(timeStyle: TimeStyle, locale: PlatformLocale) =
    AndroidDateFormat.getTimeInstance(timeStyle.toDateFormatStyle(), locale) as SimpleDateFormat

internal actual fun getDateTimeFormatInstance(dateStyle: DateStyle, timeStyle: TimeStyle, locale: PlatformLocale) =
    AndroidDateFormat.getDateTimeInstance(dateStyle.toDateFormatStyle(), timeStyle.toDateFormatStyle(), locale) as SimpleDateFormat

private fun DateStyle.toDateFormatStyle() = when (this) {
    DateStyle.SHORT -> AndroidDateFormat.SHORT
    DateStyle.MEDIUM -> AndroidDateFormat.MEDIUM
    DateStyle.LONG -> AndroidDateFormat.LONG
    DateStyle.FULL -> AndroidDateFormat.FULL
}

private fun TimeStyle.toDateFormatStyle() = when (this) {
    TimeStyle.SHORT -> AndroidDateFormat.SHORT
    TimeStyle.MEDIUM -> AndroidDateFormat.MEDIUM
    TimeStyle.LONG -> AndroidDateFormat.LONG
    TimeStyle.FULL -> AndroidDateFormat.FULL
}


// LOCALE
internal actual fun PlatformLocale.getDefaultHourCycle(): HourCycle {
    return when (DateTimePatternGenerator.getInstance(this).defaultHourCycle) {
        AndroidDateFormat.HourCycle.HOUR_CYCLE_11 -> HourCycle.HOURS_11
        AndroidDateFormat.HourCycle.HOUR_CYCLE_12 -> HourCycle.HOURS_12
        AndroidDateFormat.HourCycle.HOUR_CYCLE_23 -> HourCycle.HOURS_23
        AndroidDateFormat.HourCycle.HOUR_CYCLE_24 -> HourCycle.HOURS_24
    }
}

internal actual typealias ULocaleBuilder = ULocale.Builder