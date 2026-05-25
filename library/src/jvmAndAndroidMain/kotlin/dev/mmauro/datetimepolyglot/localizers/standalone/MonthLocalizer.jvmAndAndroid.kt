package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SimpleDateFormat
import dev.mmauro.datetimepolyglot.styles.standaloneUnicodePattern
import kotlinx.datetime.Month
import kotlinx.datetime.toJavaMonth

actual class MonthLocalizer actual constructor(
    locale: PlatformLocale,
    options: MonthOptions
) : DateTimeLocalizer<Month> {

    private val format = SimpleDateFormat(options.style.standaloneUnicodePattern, locale)

    actual override fun localize(value: Month): String {
        return format.format(value.toJavaMonth())
    }
}