package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toJavaYearMonth

actual class YearMonthLocalizer actual constructor(
    options: YearMonthOptions,
    locale: PlatformLocale
) : DateTimeLocalizer<YearMonth> {

    private val dateFormat = ComponentsOptions(dateOptions = options).toDateFormat(locale)

    actual override fun localize(value: YearMonth): String {
        return dateFormat.format(value.firstDay.toJavaLocalDate())
    }
}