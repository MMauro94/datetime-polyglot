package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.toPlainDateTime
import js.intl.DateTimeFormat
import js.temporal.PlainDateTime
import kotlinx.datetime.LocalDateTime
import kotlin.js.ExperimentalWasmJsInterop

actual class LocalDateTimeLocalizer actual constructor(
    options: LocalDateTimeOptions,
    locale: PlatformLocale
) : PolyglotDateTimeLocalizer<LocalDateTime> {

    @OptIn(ExperimentalWasmJsInterop::class)
    private val baseDateTimeLocalizer = BaseDateTimeLocalizer<PlainDateTime>(
        dateOptions = options.dateOptions,
        timeOptions = options.timeOptions,
        timeZone = null,
        locale = locale,
        formatToParts = DateTimeFormat::formatToParts,
    )

    actual override fun localize(value: LocalDateTime): String {
        return baseDateTimeLocalizer.localize(value.toPlainDateTime())
    }
}