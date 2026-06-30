package dev.mmauro.datetimepolyglot

import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeStyle
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeDirection
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeUnit
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.styles.DurationStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import kotlinx.datetime.TimeZone
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZonedDateTime
import kotlin.time.DurationUnit

internal const val ICU_UNICODE_HOUR_CYCLE_EXTENSION_KEY = "hc"

// DATE FORMAT
internal expect abstract class DateFormat

internal expect fun DateFormat.format(zonedDateTime: ZonedDateTime): String
internal expect fun DateFormat.format(localDateTime: LocalDateTime): String
internal expect fun DateFormat.format(localDate: LocalDate): String
internal expect fun DateFormat.format(localTime: LocalTime): String
internal expect fun DateFormat.format(month: Month): String

// SIMPLE DATE FORMAT
internal expect class SimpleDateFormat(pattern: String, locale: PlatformLocale) : DateFormat {
    fun toPattern(): String
}

internal expect fun getDateFormatForSkeleton(skeleton: String, locale: PlatformLocale): SimpleDateFormat
internal expect fun getDateFormatInstance(dateStyle: DateStyle, locale: PlatformLocale): SimpleDateFormat
internal expect fun getTimeFormatInstance(timeOptions: TimeOptions<TimeStyle>, locale: PlatformLocale): SimpleDateFormat
internal expect fun getDateTimeFormatInstance(
    dateStyle: DateStyle,
    timeOptions: TimeOptions<TimeStyle>,
    locale: PlatformLocale
): SimpleDateFormat

// TIMEZONE
internal expect fun TimeZone.getDisplayName(style: TimeZoneStyle, locale: PlatformLocale): String

// RELATIVE TIME
internal expect class RelativeDateTimeFormatter

internal expect fun getRelativeDateTimeFormatter(locale: PlatformLocale, style: RelativeUnitStyle): RelativeDateTimeFormatter
internal expect fun RelativeDateTimeFormatter.formatNumeric(quantity: Double, unit: RelativeUnit): String
internal expect fun RelativeDateTimeFormatter.formatDirection(direction: RelativeDirection, unit: RelativeUnit): String?
internal expect fun RelativeDateTimeFormatter.formatNow(): String?

// UNITS
internal expect class MeasureFormat {
    fun formatMeasures(vararg measures: Measure): String
}

internal expect class MeasureUnit
internal expect class Measure(number: Number, unit: MeasureUnit)

internal expect fun getMeasureFormat(locale: PlatformLocale, durationStyle: DurationStyle): MeasureFormat

// LOCALE
internal expect fun PlatformLocale.getDefaultHourCycle(): HourCycle

internal expect class ULocaleBuilder() {
    fun setLocale(locale: PlatformLocale): ULocaleBuilder
    fun setUnicodeLocaleKeyword(keyword: String, value: String): ULocaleBuilder
    fun build(): PlatformLocale
}

internal fun PlatformLocale.withHourCycle(hourCycle: HourCycle?): PlatformLocale {
    return if (hourCycle == null) {
        this
    } else {
        ULocaleBuilder()
            .setLocale(this)
            .setUnicodeLocaleKeyword(ICU_UNICODE_HOUR_CYCLE_EXTENSION_KEY, hourCycle.unicodeExtensionKeyValue)
            .build()
    }
}