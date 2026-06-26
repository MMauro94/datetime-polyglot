package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.styles.DurationStyle
import js.intl.ListFormat
import js.intl.ListFormatStyle
import js.intl.ListFormatType
import js.intl.NumberFormat
import js.intl.UnitDisplay
import js.intl.long
import js.intl.narrow
import js.intl.short
import js.intl.unit
import js.iterable.JsIterable
import js.objects.unsafeJso
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsString
import kotlin.js.toJsBigInt
import kotlin.js.toJsString
import kotlin.js.toJsArray
import kotlin.time.Duration
import kotlin.time.DurationUnit

actual class DurationLocalizer actual constructor(
    private val options: DurationOptions,
    private val locale: PlatformLocale
) : DateTimeLocalizer<Duration> {

    private val listFormat = ListFormat(locale, unsafeJso {
        type = ListFormatType.unit
        style = when (options.style) {
            DurationStyle.NARROW -> ListFormatStyle.narrow
            DurationStyle.SHORT -> ListFormatStyle.short
            DurationStyle.WIDE -> ListFormatStyle.long
        }
    })

    @OptIn(ExperimentalWasmJsInterop::class)
    actual override fun localize(value: Duration): String {
        value.requireValidAbsoluteDuration()

        val localizedUnits = options
            .detectUnits(value)
            .filter(options)
            .map { (value, unit) -> unit.numberFormat().format(value.toJsBigInt()).toJsString() }

        // For some reason JsArray doesn't implement JsIterable in Kotlin, but this works regardless. Let's suppress compiler warnings
        @Suppress("CAST_NEVER_SUCCEEDS", "UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        return listFormat.format(localizedUnits.toJsArray() as JsIterable<JsString>)
    }

    private fun DurationUnit.numberFormat(): NumberFormat {
        return NumberFormat(locale.toString(), unsafeJso {
            @OptIn(ExperimentalWasmJsInterop::class)
            style = "unit".toJsString()

            // IDE complains if we don't put the else, but then the compiler produces a warning if we put it. Let's suppress that warning.
            // RC is that DurationUnit is declared as an expect enum - https://youtrack.jetbrains.com/issue/KT-38750
            @Suppress("REDUNDANT_ELSE_IN_WHEN")
            unit = when (this@numberFormat) {
                DurationUnit.NANOSECONDS -> "nanosecond"
                DurationUnit.MICROSECONDS -> "microsecond"
                DurationUnit.MILLISECONDS -> "millisecond"
                DurationUnit.SECONDS -> "second"
                DurationUnit.HOURS -> "hour"
                DurationUnit.MINUTES -> "minute"
                DurationUnit.DAYS -> "day"
                else -> error("Unknown duration unit: ${this@numberFormat}")
            }
            unitDisplay = when (options.style) {
                DurationStyle.NARROW -> UnitDisplay.narrow
                DurationStyle.SHORT -> UnitDisplay.short
                DurationStyle.WIDE -> UnitDisplay.long
            }
        })
    }
}