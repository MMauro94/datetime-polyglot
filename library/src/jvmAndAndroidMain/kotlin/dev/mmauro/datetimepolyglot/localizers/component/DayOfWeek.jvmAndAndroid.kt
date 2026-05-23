package dev.mmauro.datetimepolyglot.localizers.component

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SimpleDateFormat
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toJavaLocalDate

actual class DayOfWeekLocalizer actual constructor(
    locale: PlatformLocale,
    options: DayOfWeekOptions
) : DateTimeLocalizer<DayOfWeek> {

    /**
     * See https://www.unicode.org/reports/tr35/tr35-dates.html#dfst-weekday
     */
    private val pattern = when (options.style) {
        DayOfWeekStyle.NARROW -> "ccccc"
        DayOfWeekStyle.SHORT -> "cccccc"
        DayOfWeekStyle.ABBREVIATED -> "ccc"
        DayOfWeekStyle.WIDE -> "cccc"
    }

    private val format = SimpleDateFormat(pattern, locale)

    actual override fun localize(value: DayOfWeek): String {
        return format.format(value.toArbitraryLocalDate().toJavaLocalDate())
    }
}