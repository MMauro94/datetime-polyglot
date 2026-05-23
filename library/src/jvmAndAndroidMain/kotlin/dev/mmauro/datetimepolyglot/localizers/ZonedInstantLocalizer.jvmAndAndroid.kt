package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.DateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDateFormatForSkeleton
import dev.mmauro.datetimepolyglot.styles.fractionalSecondsUnicodePattern
import dev.mmauro.datetimepolyglot.styles.unicodePattern
import dev.mmauro.datetimepolyglot.styles.unicodeSkeleton
import kotlinx.datetime.toJavaZoneId
import kotlin.time.Instant
import kotlin.time.toJavaInstant

actual class ZonedInstantLocalizer actual constructor(
    private val locale: PlatformLocale,
    options: ZonedInstantOptions,
) : DateTimeZonedLocalizer<Instant> {

    private val skeleton = listOfNotNull(
        options.eraStyle?.unicodePattern,
        options.yearStyle?.unicodePattern,
        options.monthStyle?.unicodePattern,
        options.dayOfWeekStyle?.unicodePattern,
        options.dayOfMonthStyle?.unicodePattern,
        options.hourStyle?.unicodeSkeleton(locale, options.dayPeriodStyle, options.hourCycle),
        options.minuteStyle?.unicodePattern,
        options.secondStyle?.unicodePattern,
        fractionalSecondsUnicodePattern(options.fractionalSecondDigits),
        options.timezoneStyle?.unicodePattern,
    ).joinToString(separator = " ")

    private val dateTimePattern = getDateFormatForSkeleton(skeleton, locale)

    actual override fun localize(value: Zoned<Instant>): String {
        val (instant, zone) = value
        val zonedDateTime = instant.toJavaInstant().atZone(zone.toJavaZoneId())

        return dateTimePattern.format(zonedDateTime)
    }
}