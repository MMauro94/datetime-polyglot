package dev.mmauro.datetimepolyglot.styles

import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultHourCycle
import dev.mmauro.datetimepolyglot.unicodePatternChar

internal fun HourStyle.unicodeSkeleton(
    locale: PlatformLocale,
    dayPeriodStyle: DayPeriodStyle?,
    hourCycle: HourCycle?,
): String {
    val actualHourCycle = hourCycle ?: locale.getDefaultHourCycle()

    val dayPeriodPattern = dayPeriodStyle?.unicodePattern
    val charCount = when (this) {
        HourStyle.NUMERIC -> 1
        HourStyle.NUMERIC_PADDED_2_DIGITS -> 2
    }

    val hourPattern = actualHourCycle.unicodePatternChar.toString().repeat(charCount)
    return if (actualHourCycle.is12Hour) {
        "$hourPattern ${dayPeriodPattern ?: "aaa"}"
    } else {
        hourPattern
    }
}
