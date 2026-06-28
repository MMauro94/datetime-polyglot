package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotValueLocalizer
import dev.mmauro.datetimepolyglot.utils.remainderUntilNextUnitBoundary
import kotlin.time.Duration

@RequiresOptIn(message = "This API is experimental. It could change or be dropped in the future without notice.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class ExperimentalTickingDurationLocalizer

/**
 * Localization options for [TickingDurationLocalizer] and [Duration.localizeTicking].
 *
 * @param durationOptions the options to pass to the underlying [DurationOptions], semantically identical
 * @param abs whether to make a duration absolute before localization (i.e. without minus sign). This does **not** affect
 * [TickingValue.nextTick] calculations, but only the localized string returned in [TickingValue.value]
 */
@ExperimentalTickingDurationLocalizer
data class TickingDurationOptions(
    val durationOptions: DurationOptions = DurationOptions(),
    val abs: Boolean = false,
)

/**
 * Localizes a [Duration] in the same exact way as a [DurationLocalizer], but this is meant to be used to localize relative values that
 * change over time.
 * For static durations use [DurationLocalizer] instead.
 *
 * The [TickingValue.value] will be the same as the output of [Duration.localize].
 *
 * For the purposes of [TickingValue.nextTick], this class assumes that the given [Duration] is obtained from a computation that linearly
 * "moves" backwards, i.e. a positive duration means something that will happen in the future, and a negative duration something that has
 * happened in the past.
 * For instance, if given the duration `10 seconds`, this class assumes that in `1.5 seconds` the [Duration] would be `8.5 seconds`.
 *
 * Examples:
 * - `1h 5m`
 * - `1 hr, 5 min`
 * - `1 hour, 5 minutes`
 */
@ExperimentalTickingDurationLocalizer
class TickingDurationLocalizer(
    private val options: TickingDurationOptions = TickingDurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotValueLocalizer<Duration, TickingValue<String>> {

    private val durationLocalizer = DurationLocalizer(options.durationOptions, locale)

    override fun localize(value: Duration): TickingValue<String> {
        require(value.isFinite()) { "duration must be finite" }

        val units = options.durationOptions.detectUnits(value)

        return TickingValue(
            value = durationLocalizer.localize(if (options.abs) value.absoluteValue else value),
            nextTick = units.minOf { (_, unit) -> value.remainderUntilNextUnitBoundary(unit) },
        )
    }
}

/**
 * Localizes this [Duration] with the given [options] in the given [locale] same as [Duration.localize], but returns a [TickingValue]
 * instead.
 *
 * @see TickingDurationLocalizer
 */
@ExperimentalTickingDurationLocalizer
fun Duration.localizeTicking(
    options: TickingDurationOptions = TickingDurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return TickingDurationLocalizer(options, locale).localize(this)
}