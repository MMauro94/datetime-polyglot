package dev.mmauro.datetimepolyglot

import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.temporal.Temporal
import com.ibm.icu.text.DateFormat as IcuDateFormat
import com.ibm.icu.text.SimpleDateFormat as IcuSimpleDateFormat

// SIMPLE DATE FORMAT
internal actual typealias SimpleDateFormat = IcuSimpleDateFormat

internal actual fun SimpleDateFormat.format(localDate: LocalDate) = format(localDate as Temporal)
internal actual fun SimpleDateFormat.format(month: Month) = format(month as Any)

// DATE FORMAT
internal actual typealias DateFormat = IcuDateFormat

internal actual fun DateFormat.format(zonedDateTime: ZonedDateTime) = format(zonedDateTime as Temporal)

// LOCALE
internal actual fun getDateFormatForSkeleton(skeleton: String, locale: PlatformLocale) =
    IcuDateFormat.getInstanceForSkeleton(skeleton, locale)
