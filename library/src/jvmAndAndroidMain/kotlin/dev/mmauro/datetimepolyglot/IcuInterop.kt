package dev.mmauro.datetimepolyglot

import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeStyle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZonedDateTime

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