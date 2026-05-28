package dev.mmauro.datetimepolyglot

import com.ibm.icu.util.ULocale
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeStyle
import java.time.LocalDate
import java.time.Month
import java.time.ZonedDateTime
import java.time.temporal.Temporal
import com.ibm.icu.text.DateFormat as IcuDateFormat
import com.ibm.icu.text.SimpleDateFormat as IcuSimpleDateFormat


// DATE FORMAT
internal actual typealias DateFormat = IcuDateFormat

internal actual fun DateFormat.format(zonedDateTime: ZonedDateTime) = format(zonedDateTime as Temporal)
internal actual fun DateFormat.format(localDate: LocalDate) = format(localDate as Temporal)
internal actual fun DateFormat.format(month: Month) = format(month as Any)

// SIMPLE DATE FORMAT
internal actual typealias SimpleDateFormat = IcuSimpleDateFormat

internal actual fun getDateFormatForSkeleton(skeleton: String, locale: PlatformLocale) =
    IcuDateFormat.getInstanceForSkeleton(skeleton, locale) as SimpleDateFormat

internal actual fun getDateFormatInstance(dateStyle: DateStyle, locale: PlatformLocale) =
    IcuDateFormat.getDateInstance(dateStyle.toDateFormatStyle(), locale) as SimpleDateFormat

internal actual fun getTimeFormatInstance(timeStyle: TimeStyle, locale: PlatformLocale) =
    IcuDateFormat.getTimeInstance(timeStyle.toDateFormatStyle(), locale) as SimpleDateFormat

internal actual fun getDateTimeFormatInstance(dateStyle: DateStyle, timeStyle: TimeStyle, locale: PlatformLocale) =
    IcuDateFormat.getDateTimeInstance(dateStyle.toDateFormatStyle(), timeStyle.toDateFormatStyle(), locale) as SimpleDateFormat

private fun DateStyle.toDateFormatStyle() = when (this) {
    DateStyle.SHORT -> IcuDateFormat.SHORT
    DateStyle.MEDIUM -> IcuDateFormat.MEDIUM
    DateStyle.LONG -> IcuDateFormat.LONG
    DateStyle.FULL -> IcuDateFormat.FULL
}

private fun TimeStyle.toDateFormatStyle() = when (this) {
    TimeStyle.SHORT -> IcuDateFormat.SHORT
    TimeStyle.MEDIUM -> IcuDateFormat.MEDIUM
    TimeStyle.LONG -> IcuDateFormat.LONG
    TimeStyle.FULL -> IcuDateFormat.FULL
}

internal actual typealias ULocaleBuilder = ULocale.Builder