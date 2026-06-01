package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.toPlainDate
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

actual class YearLocalizer actual constructor(
    private val options: YearOptions,
    private val locale: PlatformLocale
) : DateTimeLocalizer<Int> {

    actual override fun localize(value: Int): String {
        val formatOptions = unsafeJso<DateTimeFormatOptions> {
            fill(options)
        }
        val format = DateTimeFormat(locale.toString(), formatOptions)
        return format.format(LocalDate(value, Month.JANUARY, 1).toPlainDate())
    }
}