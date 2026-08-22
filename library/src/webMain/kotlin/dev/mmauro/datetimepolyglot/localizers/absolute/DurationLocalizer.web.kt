package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotValueLocalizer
import dev.mmauro.datetimepolyglot.styles.DurationStyle
import js.intl.DurationFormat
import js.intl.DurationFormatDisplayOption
import js.intl.DurationFormatStyle
import js.intl.DurationFormatUnit
import js.intl.always
import js.intl.days
import js.intl.hours
import js.intl.long
import js.intl.microseconds
import js.intl.milliseconds
import js.intl.minutes
import js.intl.nanoseconds
import js.intl.narrow
import js.intl.seconds
import js.intl.short
import js.numbers.JsInt
import js.numbers.JsNumbers.toJsInt
import js.objects.Record
import js.objects.unsafeJso
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.time.Duration
import kotlin.time.DurationUnit

public actual class DurationLocalizer actual constructor(
    internal actual val options: DurationOptions,
    private val locale: PlatformLocale
) : PolyglotValueLocalizer<Duration, String> {

    @ExperimentalWasmJsInterop
    actual override fun localize(value: Duration): String {
        return value.internalLocalize(options, locale) { filteredUnits ->
            val durationFormat = DurationFormat(locale, unsafeJso {
                style = when (options.style) {
                    DurationStyle.NARROW -> DurationFormatStyle.narrow
                    DurationStyle.SHORT -> DurationFormatStyle.short
                    DurationStyle.WIDE -> DurationFormatStyle.long
                }
                for ((_, unit) in filteredUnits) {
                    @Suppress("REDUNDANT_ELSE_IN_WHEN")
                    when (unit) {
                        DurationUnit.NANOSECONDS -> this.nanosecondsDisplay = DurationFormatDisplayOption.always
                        DurationUnit.MICROSECONDS -> this.microsecondsDisplay = DurationFormatDisplayOption.always
                        DurationUnit.MILLISECONDS -> this.millisecondsDisplay = DurationFormatDisplayOption.always
                        DurationUnit.SECONDS -> this.secondsDisplay = DurationFormatDisplayOption.always
                        DurationUnit.MINUTES -> this.minutesDisplay = DurationFormatDisplayOption.always
                        DurationUnit.HOURS -> this.hoursDisplay = DurationFormatDisplayOption.always
                        DurationUnit.DAYS -> this.daysDisplay = DurationFormatDisplayOption.always
                        else -> error("Unknown duration unit: $this")
                    }
                }
            })

            durationFormat.format(unsafeJso<Record<DurationFormatUnit, JsInt>> {
                for ((value, unit) in filteredUnits) {
                    this[unit.toDurationFormatUnit()] = value.toInt().toJsInt()
                }
            })
        }
    }

    private fun DurationUnit.toDurationFormatUnit(): DurationFormatUnit {
        // IDE complains if we don't put the else, but then the compiler produces a warning if we put it. Let's suppress that warning.
        // RC is that DurationUnit is declared as an expect enum - https://youtrack.jetbrains.com/issue/KT-38750
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        return when (this) {
            DurationUnit.NANOSECONDS -> DurationFormatUnit.nanoseconds
            DurationUnit.MICROSECONDS -> DurationFormatUnit.microseconds
            DurationUnit.MILLISECONDS -> DurationFormatUnit.milliseconds
            DurationUnit.SECONDS -> DurationFormatUnit.seconds
            DurationUnit.MINUTES -> DurationFormatUnit.minutes
            DurationUnit.HOURS -> DurationFormatUnit.hours
            DurationUnit.DAYS -> DurationFormatUnit.days
            else -> error("Unknown duration unit: $this")
        }
    }
}