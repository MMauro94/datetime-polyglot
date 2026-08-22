package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SimpleDateFormat
import dev.mmauro.datetimepolyglot.format
import dev.mmauro.datetimepolyglot.styles.standaloneUnicodePattern
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toJavaLocalDate

public actual class DayOfWeekLocalizer actual constructor(
    options: DayOfWeekOptions,
    locale: PlatformLocale,
) : PolyglotDateTimeLocalizer<DayOfWeek> {

    private val format = SimpleDateFormat(options.style.standaloneUnicodePattern, locale)

    actual override fun localize(value: DayOfWeek): String {
        return format.format(value.toArbitraryLocalDate().toJavaLocalDate())
    }
}