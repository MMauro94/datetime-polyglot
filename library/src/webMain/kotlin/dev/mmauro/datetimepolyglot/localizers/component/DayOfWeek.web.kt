package dev.mmauro.datetimepolyglot.localizers.component

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.toJsDate
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.intl.WeekdayFormat
import js.intl.long
import js.intl.narrow
import js.intl.short
import js.objects.unsafeJso
import kotlinx.datetime.DayOfWeek

actual class DayOfWeekLocalizer actual constructor(
    locale: PlatformLocale,
    options: DayOfWeekOptions,
) : DateTimeLocalizer<DayOfWeek> {

    /**
     * See https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl/DateTimeFormat/DateTimeFormat#weekday
     */
    private val weekdayFormat = when (options.style) {
        DayOfWeekStyle.NARROW -> WeekdayFormat.narrow
        // There is no support for two-letters, fall back on three
        DayOfWeekStyle.SHORT, DayOfWeekStyle.ABBREVIATED -> WeekdayFormat.short
        DayOfWeekStyle.WIDE -> WeekdayFormat.long
    }

    private val formatOptions: DateTimeFormatOptions = unsafeJso { weekday = weekdayFormat }

    private val format = DateTimeFormat(locale.toString(), formatOptions)

    actual override fun localize(value: DayOfWeek): String {
        return format.format(value.toArbitraryLocalDate().toJsDate())
    }
}