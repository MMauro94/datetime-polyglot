package dev.mmauro.datetimepolyglot

import java.time.temporal.Temporal

internal expect class SimpleDateFormat(pattern: String, locale: PlatformLocale) {
    fun format(temporal: Any): String
}

internal expect abstract class DateFormat {
    fun format(temporal: Any): String
}

internal expect fun getDateFormatForSkeleton(skeleton: String, locale: PlatformLocale): DateFormat

internal expect fun PlatformLocale.getDefaultHourCycle(): HourCycle