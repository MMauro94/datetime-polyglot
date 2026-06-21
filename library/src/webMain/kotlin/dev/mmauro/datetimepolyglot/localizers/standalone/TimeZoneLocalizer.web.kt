package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.styles.toMonthFormat
import dev.mmauro.datetimepolyglot.styles.toTimeZoneNameFormat
import js.array.asSequence
import js.date.Date
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatOptions
import js.objects.unsafeJso
import kotlinx.datetime.TimeZone

// Just a random date to format in order to get the time zone name
private val REFERENCE_DATE = Date(0.0)

actual class TimeZoneLocalizer actual constructor(
    private val options: TimeZoneOptions,
    private val locale: PlatformLocale
) : DateTimeLocalizer<TimeZone> {

    actual override fun localize(value: TimeZone): String {
        if (options.style == TimeZoneStyle.Generic.ID) {
            // JS doesn't natively support formatting the time zone ID, but we can just return it manually
            return value.id
        }

        val formatOptions = unsafeJso<DateTimeFormatOptions> {
            timeZone = value.id
            timeZoneName = options.style.toTimeZoneNameFormat()
        }
        val format = DateTimeFormat(locale.toString(), formatOptions)

        return format.formatToParts(REFERENCE_DATE).asSequence().single { it.type == "timeZoneName" }.value
    }
}