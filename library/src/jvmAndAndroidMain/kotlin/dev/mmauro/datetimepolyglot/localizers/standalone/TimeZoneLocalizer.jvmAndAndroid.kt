package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SimpleDateFormat
import dev.mmauro.datetimepolyglot.getDisplayName
import dev.mmauro.datetimepolyglot.styles.standaloneUnicodePattern
import dev.mmauro.datetimepolyglot.styles.unicodePattern
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone

actual class TimeZoneLocalizer actual constructor(
    private val options: TimeZoneOptions,
    private val locale: PlatformLocale
) : DateTimeLocalizer<TimeZone> {

    actual override fun localize(value: TimeZone): String {
        return value.getDisplayName(options.style, locale)
    }
}