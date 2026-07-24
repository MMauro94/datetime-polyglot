package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.utils.joinDateAndTime
import js.array.asSequence
import js.intl.DateTimeFormat
import js.intl.DateTimeFormatPart
import js.intl.TimeZoneNameFormat
import js.intl.shortOffset
import js.objects.unsafeJso
import kotlinx.datetime.TimeZone
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsArray

@OptIn(ExperimentalWasmJsInterop::class)
internal class BaseDateTimeLocalizer<T>(
    private val dateOptions: DateOptions,
    private val timeOptions: TimeOptions<*>,
    private val timeZone: TimeZone?,
    private val locale: PlatformLocale,
    private val formatToParts: DateTimeFormat.(T) -> JsArray<out DateTimeFormatPart>,
    private val partMapper: (DateTimeFormatPart) -> String = { it.value },
) : PolyglotDateTimeLocalizer<T> {

    private val localizer = when (dateOptions.styleOptions) {
        is DateStyle -> when (timeOptions.styleOptions) {
            is TimeStyle -> Full()
            is TimeComponents -> Split()
        }

        is DateComponents -> when (timeOptions.styleOptions) {
            is TimeStyle -> Split()
            is TimeComponents -> Full()
        }
    }


    override fun localize(value: T): String {
        return localizer.localize(value)
    }

    // When date and time options are cocondarnt (both styles or both components), we use a single DateTimeFormat
    inner class Full : PolyglotDateTimeLocalizer<T> {
        val format = DateTimeFormat(locale.toString(), unsafeJso {
            fill(dateOptions.toComponentOptions())
            fill(timeOptions.toComponentOptions(), timeZoneIdFallback = TimeZoneNameFormat.shortOffset)

            this@BaseDateTimeLocalizer.timeZone?.let {
                this.timeZone = it.id
            }
        })

        override fun localize(value: T): String {
            return format.format(value)
        }
    }

    // The JS Intl API does not allow to mix-and-match styles and component options for date and time: either they are both styles or they
    // are both components
    // To work around this limitation, this localizer localizes twice (once for date and once for time) and then joins via joinDateAndTime
    inner class Split : PolyglotDateTimeLocalizer<T> {
        private val dateFormat = DateTimeFormat(
            locales = locale.toString(),
            options = unsafeJso {
                fill(dateOptions.toComponentOptions())
            }
        )
        private val timeFormat = DateTimeFormat(
            locales = locale.toString(),
            options = unsafeJso {
                fill(timeOptions.toComponentOptions(), timeZoneIdFallback = TimeZoneNameFormat.shortOffset)

                this@BaseDateTimeLocalizer.timeZone?.let {
                    this.timeZone = it.id
                }
            }
        )
        private val joinerStyle = DateStyle.detectDateTimeJoinerStyle(dateOptions.styleOptions)

        override fun localize(value: T): String {
            return joinDateAndTime(
                locale = locale,
                style = joinerStyle,
                date = dateFormat.format(value),
                time = timeFormat.format(value)
            )
        }
    }

    private fun DateTimeFormat.format(value: T): String {
        return this.formatToParts(value).asSequence().joinToString(separator = "", transform = partMapper)
    }
}