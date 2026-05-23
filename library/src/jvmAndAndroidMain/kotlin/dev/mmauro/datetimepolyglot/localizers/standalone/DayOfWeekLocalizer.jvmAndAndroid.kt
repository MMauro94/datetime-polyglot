package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SimpleDateFormat
import dev.mmauro.datetimepolyglot.styles.standaloneUnicodePattern
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toJavaLocalDate

actual class DayOfWeekLocalizer actual constructor(
    locale: PlatformLocale,
    options: DayOfWeekOptions
) : DateTimeLocalizer<DayOfWeek> {

    private val format = SimpleDateFormat(options.style.standaloneUnicodePattern, locale)

    actual override fun localize(value: DayOfWeek): String {
        return format.format(value.toArbitraryLocalDate().toJavaLocalDate())
    }
}