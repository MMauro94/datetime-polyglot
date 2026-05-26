package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.styles.toWeekdayFormat
import dev.mmauro.datetimepolyglot.toPlainDate
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlinx.datetime.DayOfWeek

actual class DayOfWeekLocalizer actual constructor(
    options: DayOfWeekOptions,
    locale: PlatformLocale,
) : DateTimeLocalizer<DayOfWeek> {

    private val formatOptions: DateTimeFormatOptions = unsafeJso { weekday = options.style.toWeekdayFormat() }

    private val format = DateTimeFormat(locale.toString(), formatOptions)

    actual override fun localize(value: DayOfWeek): String {
        return format.format(value.toArbitraryLocalDate().toPlainDate())
    }
}