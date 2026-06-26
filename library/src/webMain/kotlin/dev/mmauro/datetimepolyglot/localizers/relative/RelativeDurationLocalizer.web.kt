package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.RelativeLocalizer
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.styles.DurationStyle
import js.intl.RelativeTimeFormat
import js.intl.RelativeTimeFormatNumeric
import js.intl.RelativeTimeFormatStyle
import js.intl.RelativeTimeFormatUnit
import js.intl.always
import js.intl.day
import js.intl.hour
import js.intl.long
import js.intl.minute
import js.intl.narrow
import js.intl.second
import js.intl.short
import js.objects.unsafeJso
import kotlin.time.Duration
import kotlin.time.DurationUnit

actual class RelativeDurationLocalizer actual constructor(
    private val options: RelativeDurationOptions,
    private val locale: PlatformLocale
) : RelativeLocalizer<Duration> {

    private val relativeTimeFormat = RelativeTimeFormat(locale, unsafeJso {
        style = when (options.style) {
            DurationStyle.NARROW -> RelativeTimeFormatStyle.narrow
            DurationStyle.SHORT -> RelativeTimeFormatStyle.short
            DurationStyle.WIDE -> RelativeTimeFormatStyle.long
        }

        numeric = RelativeTimeFormatNumeric.always
    })

    actual override fun localize(value: Duration): TickingValue<String> {
        return value.internalLocalize(options, locale) { value, unit ->
            // IDE complains if we don't put the else, but then the compiler produces a warning if we put it. Let's suppress that warning.
            // RC is that DurationUnit is declared as an expect enum - https://youtrack.jetbrains.com/issue/KT-38750
            @Suppress("REDUNDANT_ELSE_IN_WHEN")
            val jsUnit = when (unit) {
                DurationUnit.NANOSECONDS -> error("nanosecond unit not supported")
                DurationUnit.MICROSECONDS -> error("microsecond unit not supported")
                DurationUnit.MILLISECONDS -> error("millisecond unit not supported")
                DurationUnit.SECONDS -> RelativeTimeFormatUnit.second
                DurationUnit.MINUTES -> RelativeTimeFormatUnit.minute
                DurationUnit.HOURS -> RelativeTimeFormatUnit.hour
                DurationUnit.DAYS -> RelativeTimeFormatUnit.day
                else -> error("Unknown duration unit: $unit")
            }
            relativeTimeFormat.format(value.toDouble(), jsUnit)
        }
    }
}