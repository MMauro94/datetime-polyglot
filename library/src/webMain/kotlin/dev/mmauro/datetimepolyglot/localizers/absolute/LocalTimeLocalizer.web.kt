package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.toPlainTime
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlinx.datetime.LocalTime

public actual class LocalTimeLocalizer actual constructor(
    private val options: LocalTimeOptions<LocalTimeStyleOptions>,
    private val locale: PlatformLocale
) : PolyglotDateTimeLocalizer<LocalTime> {

    public actual constructor(options: LocalTimeStyleOptions, locale: PlatformLocale) : this(LocalTimeOptions(options), locale)

    actual override fun localize(value: LocalTime): String {
        val formatOptions = unsafeJso<DateTimeFormatOptions> {
            fill(options.toComponentOptions())
        }
        val format = DateTimeFormat(locale.toString(), formatOptions)
        return format.format(value.toPlainTime())
    }
}