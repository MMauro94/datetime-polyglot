package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.styles.toMonthFormat
import dev.mmauro.datetimepolyglot.toPlainDate
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

public actual class MonthLocalizer actual constructor(
    options: MonthOptions,
    locale: PlatformLocale,
) : PolyglotDateTimeLocalizer<Month> {

    private val formatOptions: DateTimeFormatOptions = unsafeJso { month = options.style.toMonthFormat() }

    private val format = DateTimeFormat(locale.toString(), formatOptions)

    actual override fun localize(value: Month): String {
        return format.format(LocalDate(0, value, 1).toPlainDate())
    }
}
