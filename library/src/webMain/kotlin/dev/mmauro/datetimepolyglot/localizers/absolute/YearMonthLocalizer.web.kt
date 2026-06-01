package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.toPlainDate
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth

actual class YearMonthLocalizer actual constructor(
    private val options: YearMonthOptions,
    private val locale: PlatformLocale
) : DateTimeLocalizer<YearMonth> {
    actual override fun localize(value: YearMonth): String {
        val formatOptions = unsafeJso<DateTimeFormatOptions> {
            fill(options)
        }
        val format = DateTimeFormat(locale.toString(), formatOptions)
        return format.format(value.firstDay.toPlainDate())
    }
}