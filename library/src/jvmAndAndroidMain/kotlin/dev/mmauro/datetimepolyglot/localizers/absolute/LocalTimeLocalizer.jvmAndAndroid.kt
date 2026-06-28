package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.format
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalTime

actual class LocalTimeLocalizer actual constructor(
    options: TimeOptions<TimeStyleOptions.Local>,
    locale: PlatformLocale
) : PolyglotDateTimeLocalizer<LocalTime> {

    actual constructor(options: TimeStyleOptions.Local, locale: PlatformLocale) : this(TimeOptions(options), locale)

    private val dateFormat = ComponentsOptions(timeOptions = options.toComponentOptions()).toDateFormat(locale)

    actual override fun localize(value: LocalTime): String {
        return dateFormat.format(value.toJavaLocalTime())
    }
}