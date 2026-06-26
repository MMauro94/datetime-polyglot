package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.RelativeLocalizer
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.absolute.DurationLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.DurationOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.detectUnits
import dev.mmauro.datetimepolyglot.utils.remainderUntilNextUnitBoundary
import kotlin.time.Duration

@RequiresOptIn(message = "This API is experimental. It could change or be dropped in the future without notice.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class ExperimentalMultiUnitRelativeDuration

/**
 * Formats a [Duration] in the same way as a [DurationLocalizer] does, but this is meant to be used to format relative values that change
 * over time.
 *
 * The [localize] function returns a [TickingValue] value indicating when a new [localize] call is required to update the value.
 *
 * For the purposes of [TickingValue.nextTick], this class assumes that the given [Duration] is obtained from a computation that linearly
 * "moves" backwards, i.e. a positive duration means something that will happen in the future, and a negative duration something that has
 * happened in the past.
 * For instance, if given the duration `10 seconds`, this class assumes that in `1.5 seconds` the [Duration] would be `8.5 seconds`.
 *
 * Note that, differently from [DurationLocalizer], this class allows negative durations to be passed. However, no actual difference in
 * the output string is present.
 * The words "ago" and "in" are not included in the output because localized relative-time phrases for multi-unit durations are not
 * standardized and are not provided by ICU/CLDR.
 */
@ExperimentalMultiUnitRelativeDuration
class MultiUnitRelativeDurationLocalizer(
    val options: DurationOptions = DurationOptions(),
    val locale: PlatformLocale = getDefaultLocale(),
) : RelativeLocalizer<Duration> {

    private val durationLocalizer = DurationLocalizer(options, locale)

    override fun localize(value: Duration): TickingValue<String> {
        require(value.isFinite()) { "duration must be finite" }

        val absDuration = value.absoluteValue
        val units = options.detectUnits(absDuration)

        return TickingValue(
            value = durationLocalizer.localize(absDuration),
            nextTick = units.minOf { (_, unit) -> value.remainderUntilNextUnitBoundary(unit) },
        )
    }
}

@ExperimentalMultiUnitRelativeDuration
fun Duration.localizeRelativeMultiUnit(
    options: DurationOptions = DurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return MultiUnitRelativeDurationLocalizer(options, locale).localize(this)
}