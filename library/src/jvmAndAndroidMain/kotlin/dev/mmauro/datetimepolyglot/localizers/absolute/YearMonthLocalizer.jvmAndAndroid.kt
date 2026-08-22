package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toJavaLocalDate

public actual class YearMonthLocalizer actual constructor(
    options: YearMonthOptions,
    locale: PlatformLocale
) : PolyglotDateTimeLocalizer<YearMonth> {

    private val dateFormat = ComponentsOptions(dateOptions = options).toDateFormat(locale)

    actual override fun localize(value: YearMonth): String {
        return dateFormat.format(value.firstDay.toJavaLocalDate())
    }
}