package dev.mmauro.datetimepolyglot

import java.time.LocalDate
import java.time.Month
import java.time.ZonedDateTime

// SIMPLE DATE FORMAT
internal expect class SimpleDateFormat(pattern: String, locale: PlatformLocale)

internal expect fun SimpleDateFormat.format(localDate: LocalDate): String
internal expect fun SimpleDateFormat.format(month: Month): String

// DATE FORMAT
internal expect abstract class DateFormat

internal expect fun DateFormat.format(zonedDateTime: ZonedDateTime): String

internal expect fun getDateFormatForSkeleton(skeleton: String, locale: PlatformLocale): DateFormat

// LOCALE
internal expect fun PlatformLocale.getDefaultHourCycle(): HourCycle