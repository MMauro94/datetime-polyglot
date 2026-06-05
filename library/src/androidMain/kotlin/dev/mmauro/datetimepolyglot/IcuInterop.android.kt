package dev.mmauro.datetimepolyglot

import android.icu.text.DateFormat as AndroidDateFormat
import android.icu.text.DateTimePatternGenerator
import android.icu.util.TimeZone
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeStyle
import android.icu.text.SimpleDateFormat as AndroidSimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.Temporal

// DATE FORMAT
internal actual typealias DateFormat = AndroidDateFormat

internal actual fun DateFormat.format(month: Month): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(month as Any)
    } else {
        timeZone = TimeZone.GMT_ZONE
        format(LocalDate.of(0, month, 1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
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

internal actual fun DateFormat.format(localTime: LocalTime): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(localTime as Temporal)
    } else {
        timeZone = TimeZone.GMT_ZONE
        format(localTime.atDate(LocalDate.ofEpochDay(0)).toInstant(ZoneOffset.UTC).toEpochMilli())
    }
}

internal actual fun DateFormat.format(zonedDateTime: ZonedDateTime): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(zonedDateTime as Temporal)
    } else {
        timeZone = TimeZone.getTimeZone(zonedDateTime.zone.id)
        format(zonedDateTime.toInstant().toEpochMilli())
    }
}

// SIMPLE DATE FORMAT
internal actual typealias SimpleDateFormat = AndroidSimpleDateFormat

internal actual fun getDateFormatForSkeleton(skeleton: String, locale: PlatformLocale) =
    DateFormat.getInstanceForSkeleton(skeleton, locale) as SimpleDateFormat

internal actual fun getDateFormatInstance(dateStyle: DateStyle, locale: PlatformLocale) =
    AndroidDateFormat.getDateInstance(dateStyle.toDateFormatStyle(), locale) as SimpleDateFormat

internal actual fun getTimeFormatInstance(timeOptions: TimeOptions<TimeStyle>, locale: PlatformLocale): SimpleDateFormat {
    val format = AndroidDateFormat.getTimeInstance(
        timeOptions.styleOptions.toDateFormatStyle(),
        locale.withHourCycle(timeOptions.hourCycle)
    ) as SimpleDateFormat

    return format.overrideHourCycleIfNecessary(locale, timeOptions.hourCycle)
}

internal actual fun getDateTimeFormatInstance(
    dateStyle: DateStyle,
    timeOptions: TimeOptions<TimeStyle>,
    locale: PlatformLocale
): SimpleDateFormat {
    val format = AndroidDateFormat.getDateTimeInstance(
        dateStyle.toDateFormatStyle(),
        timeOptions.styleOptions.toDateFormatStyle(),
        locale.withHourCycle(timeOptions.hourCycle)
    ) as SimpleDateFormat

    return format.overrideHourCycleIfNecessary(locale, timeOptions.hourCycle)
}

private fun SimpleDateFormat.overrideHourCycleIfNecessary(locale: PlatformLocale, hourCycle: HourCycle?): SimpleDateFormat {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM || hourCycle == null) {
        // Android versions >= SDK 35 use ICU >= 75.1 that fixes an issue where getting a date/time format with a locale using an overridden
        // hour cycle wasn't taking the hour cycle into account.
        // This was fixed in ICU 74.1: https://github.com/unicode-org/icu/commit/8817c25c1eac3a0a1b66ac7437e24977e2b93887
        this
    } else {
        var skeleton = toPattern()
        for (hc in HourCycle.entries) {
            skeleton = skeleton.replace(hc.unicodePatternChar, hourCycle.unicodePatternChar)
        }
        getDateFormatForSkeleton(skeleton, locale)
    }
}

private fun DateStyle.toDateFormatStyle() = when (this) {
    DateStyle.SHORT -> AndroidDateFormat.SHORT
    DateStyle.MEDIUM -> AndroidDateFormat.MEDIUM
    DateStyle.LONG -> AndroidDateFormat.LONG
    DateStyle.FULL -> AndroidDateFormat.FULL
}

private fun TimeStyle.toDateFormatStyle() = when (this) {
    TimeStyle.Local.SHORT -> AndroidDateFormat.SHORT
    TimeStyle.Local.MEDIUM -> AndroidDateFormat.MEDIUM
    TimeStyle.Zoned.LONG -> AndroidDateFormat.LONG
    TimeStyle.Zoned.FULL -> AndroidDateFormat.FULL
}


// LOCALE
internal actual fun PlatformLocale.getDefaultHourCycle(): HourCycle {
    val patternGenerator = DateTimePatternGenerator.getInstance(this)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        patternGenerator.getDefaultHourCycleAndroid33()
    } else {
        val pattern = patternGenerator.getBestPattern("j")
        when {
            'K' in pattern -> HourCycle.HOURS_11
            'h' in pattern -> HourCycle.HOURS_12
            'H' in pattern -> HourCycle.HOURS_23
            'k' in pattern -> HourCycle.HOURS_24
            else -> error("Unable to detect hour cycle for locale $this. Best pattern: $pattern")
        }
    }
}

internal actual typealias ULocaleBuilder = ULocale.Builder