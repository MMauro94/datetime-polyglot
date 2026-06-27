package dev.mmauro.datetimepolyglot

import android.icu.text.DateTimePatternGenerator
import android.icu.text.DisplayContext
import android.icu.util.ULocale
import android.os.Build
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeStyle
import dev.mmauro.datetimepolyglot.styles.DurationStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaZoneId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.Temporal
import kotlin.time.DurationUnit
import android.icu.text.DateFormat as AndroidDateFormat
import android.icu.text.MeasureFormat as IcuMeasureFormat
import android.icu.text.RelativeDateTimeFormatter as IcuRelativeDateTimeFormatter
import android.icu.text.SimpleDateFormat as AndroidSimpleDateFormat
import android.icu.util.Measure as IcuMeasure
import android.icu.util.MeasureUnit as IcuMeasureUnit
import android.icu.util.TimeZone as IcuTimeZone

// DATE FORMAT
internal actual typealias DateFormat = AndroidDateFormat

internal actual fun DateFormat.format(month: Month): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(month as Any)
    } else {
        timeZone = IcuTimeZone.GMT_ZONE
        format(LocalDate.of(0, month, 1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
    }
}

internal actual fun DateFormat.format(zonedDateTime: ZonedDateTime): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(zonedDateTime as Temporal)
    } else {
        timeZone = IcuTimeZone.getTimeZone(zonedDateTime.zone.id)
        format(zonedDateTime.toInstant().toEpochMilli())
    }
}

internal actual fun DateFormat.format(localDateTime: LocalDateTime): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(localDateTime as Temporal)
    } else {
        timeZone = IcuTimeZone.GMT_ZONE
        format(localDateTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli())
    }
}

internal actual fun DateFormat.format(localDate: LocalDate): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(localDate as Temporal)
    } else {
        timeZone = IcuTimeZone.GMT_ZONE
        format(localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
    }
}

internal actual fun DateFormat.format(localTime: LocalTime): String {
    return if (Build.VERSION.SDK_INT >= 37) {
        format(localTime as Temporal)
    } else {
        timeZone = IcuTimeZone.GMT_ZONE
        format(localTime.atDate(LocalDate.ofEpochDay(0)).toInstant(ZoneOffset.UTC).toEpochMilli())
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

internal fun DateStyle.toDateFormatStyle() = when (this) {
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

// TIMEZONE
internal actual fun TimeZone.getDisplayName(style: TimeZoneStyle, locale: PlatformLocale): String {
    if (style == TimeZoneStyle.Generic.ID) {
        return id
    }

    val javaTimeZone = this.toJavaZoneId()
    return IcuTimeZone.getTimeZone(javaTimeZone.id, IcuTimeZone.TIMEZONE_JDK).getDisplayName(false, style.toIcuStyle(), locale)
}

private fun TimeZoneStyle.toIcuStyle() = when (this) {
    // Generic
    TimeZoneStyle.Generic.ID -> error("Unsupported ICU TimeZone style ID: should be handled separately")
    TimeZoneStyle.Generic.NON_LOCATION_SHORT -> IcuTimeZone.SHORT_GENERIC
    TimeZoneStyle.Generic.NON_LOCATION_LONG -> IcuTimeZone.LONG_GENERIC
    TimeZoneStyle.Generic.LOCATION -> IcuTimeZone.GENERIC_LOCATION

    // Specific
    TimeZoneStyle.Specific.NON_LOCATION_SHORT -> IcuTimeZone.SHORT
    TimeZoneStyle.Specific.NON_LOCATION_LONG -> IcuTimeZone.LONG

    // GMT
    TimeZoneStyle.Gmt.SHORT -> IcuTimeZone.SHORT_GMT
    TimeZoneStyle.Gmt.LONG -> IcuTimeZone.LONG_GMT
}

// RELATIVE TIME

internal actual typealias RelativeDateTimeFormatter = IcuRelativeDateTimeFormatter

internal actual fun getRelativeDateTimeFormatter(locale: PlatformLocale, style: DurationStyle): RelativeDateTimeFormatter {
    val style = when (style) {
        DurationStyle.NARROW -> IcuRelativeDateTimeFormatter.Style.NARROW
        DurationStyle.SHORT -> IcuRelativeDateTimeFormatter.Style.SHORT
        DurationStyle.WIDE -> IcuRelativeDateTimeFormatter.Style.LONG
    }
    return IcuRelativeDateTimeFormatter.getInstance(locale, null, style, DisplayContext.CAPITALIZATION_NONE)
}

internal actual fun RelativeDateTimeFormatter.formatNumeric(quantity: Long, unit: DurationUnit): String {
    val relativeUnit = when (unit) {
        DurationUnit.NANOSECONDS -> error("nanosecond unit not supported")
        DurationUnit.MICROSECONDS -> error("microsecond unit not supported")
        DurationUnit.MILLISECONDS -> error("millisecond unit not supported")
        DurationUnit.SECONDS -> IcuRelativeDateTimeFormatter.RelativeDateTimeUnit.SECOND
        DurationUnit.MINUTES -> IcuRelativeDateTimeFormatter.RelativeDateTimeUnit.MINUTE
        DurationUnit.HOURS -> IcuRelativeDateTimeFormatter.RelativeDateTimeUnit.HOUR
        DurationUnit.DAYS -> IcuRelativeDateTimeFormatter.RelativeDateTimeUnit.DAY
    }
    return formatNumeric(quantity.toDouble(), relativeUnit)
}


// UNITS
internal actual typealias MeasureFormat = IcuMeasureFormat
internal actual typealias MeasureUnit = IcuMeasureUnit
internal actual typealias Measure = IcuMeasure

internal actual fun getMeasureFormat(locale: PlatformLocale, durationStyle: DurationStyle): MeasureFormat {
    return IcuMeasureFormat.getInstance(locale, durationStyle.toIcuFormatWidth())
}

private fun DurationStyle.toIcuFormatWidth() = when (this) {
    DurationStyle.NARROW -> IcuMeasureFormat.FormatWidth.NARROW
    DurationStyle.SHORT -> IcuMeasureFormat.FormatWidth.SHORT
    DurationStyle.WIDE -> IcuMeasureFormat.FormatWidth.WIDE
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
