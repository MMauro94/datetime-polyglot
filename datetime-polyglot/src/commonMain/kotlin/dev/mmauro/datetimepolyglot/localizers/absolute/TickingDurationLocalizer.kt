package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.ClockWrapper
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.localizeNow
import dev.mmauro.datetimepolyglot.utils.remainderUntilNextUnitBoundary
import kotlinx.coroutines.flow.Flow
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

@RequiresOptIn(message = "This API is experimental. It could change or be dropped in the future without notice.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class ExperimentalTickingDurationLocalizer

/**
 * Localization options for [TickingDurationLocalizer] and [localizeTicking].
 *
 * @param durationOptions the options to pass to the underlying [DurationOptions], semantically identical
 * @param abs whether to make a duration absolute before localization (i.e. without minus sign). This does **not** affect
 * [TickingValue.nextTick] calculations, but only the localized string returned in [TickingValue.value]
 */
@ExperimentalTickingDurationLocalizer
public data class TickingDurationOptions(
    val durationOptions: DurationOptions = DurationOptions(),
    val abs: Boolean = false,
) : PolyglotLocalizerOptions<TickingDurationLocalizer> {
    override fun localizer(locale: PlatformLocale): TickingDurationLocalizer = TickingDurationLocalizer(this, locale)
}

/**
 * Localizes a [Duration] in the same exact way as a [DurationLocalizer], but this is meant to be used to localize duration values that
 * change over time (e.g. a countdown).
 * For static durations use [DurationLocalizer] instead.
 *
 * The [TickingValue.value] will be the same as the output of [Duration.localize].
 *
 * For the purposes of [TickingValue.nextTick], this class assumes that the given [Duration] is obtained from a computation that linearly
 * "moves" backwards, i.e. a positive duration means something that will happen in the future, and a negative duration something that has
 * happened in the past.
 * For instance, if given the duration `10 seconds`, this class assumes that in `1.5 seconds` the [Duration] would be `8.5 seconds`.
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [Duration.localizeTicking] for one-off localizations.
 *
 * Examples:
 * - `1h 5m`
 * - `1 hr, 5 min`
 * - `1 hour, 5 minutes`
 */
@ExperimentalTickingDurationLocalizer
public class TickingDurationLocalizer(
    private val options: TickingDurationOptions = TickingDurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotValueLocalizer<Duration, TickingValue<String>>, PolyglotReferenceValueLocalizer<Instant, String> {

    private val durationLocalizer = DurationLocalizer(options.durationOptions, locale)

    override fun localize(value: Duration): TickingValue<String> {
        require(value.isFinite()) { "duration must be finite" }

        val units = options.durationOptions.detectUnits(value)

        return TickingValue(
            value = durationLocalizer.localize(if (options.abs) value.absoluteValue else value),
            nextTick = units.minOf { (_, unit) -> value.remainderUntilNextUnitBoundary(unit) },
        )
    }

    public fun localize(value: Instant, reference: Instant): TickingValue<String> {
        return localize(value - reference)
    }

    override fun localize(value: Instant, reference: Zoned<Instant>): TickingValue<String> {
        return localize(value, reference.value)
    }
}

/**
 * Localizes this [Duration] with the given [options] in the given [locale] same as [Duration.localize], but returns a [TickingValue]
 * instead.
 *
 * @see TickingDurationLocalizer
 */
@ExperimentalTickingDurationLocalizer
public fun Duration.localizeTicking(
    options: TickingDurationOptions = TickingDurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return TickingDurationLocalizer(options, locale).localize(this)
}

/**
 * Calculates the diff between this [Instant] and [reference], and localizes the result with the given [options] in the given [locale].
 *
 * @see localizeTicking
 * @see TickingDurationLocalizer
 */
@ExperimentalTickingDurationLocalizer
public fun Instant.localizeDiff(
    reference: Instant,
    options: TickingDurationOptions = TickingDurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return TickingDurationLocalizer(options, locale).localize(this, reference)
}

/**
 * Calculates the diff between this [Instant] and the current instant of [clock], and localizes the result with the given [options] in the
 * given [locale].
 *
 * @see Duration.localizeTicking
 * @see TickingDurationLocalizer
 */
@ExperimentalTickingDurationLocalizer
public fun Instant.localizeDiffNow(
    options: TickingDurationOptions = TickingDurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
    // Timezone is useless here
): TickingValue<String> {
    return TickingDurationLocalizer(options, locale).localizeNow(this, clock)
}

/**
 * Calculates the diff between this [Instant] and [clock], and localizes the result with the given [options] in the given [locale],
 * returning a [Flow] that automatically receives new localizations as they are needed.
 *
 * @see Duration.localizeTicking
 * @see TickingDurationLocalizer
 * @see localizeAsFlow
 */
@ExperimentalTickingDurationLocalizer
public fun Instant.localizeDiffAsFlow(
    options: TickingDurationOptions = TickingDurationOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Flow<ClockWrapper> = SYSTEM_CLOCK,
    // Timezone is useless here
): Flow<String> {
    return TickingDurationLocalizer(options, locale).localizeAsFlow(this, clock)
}
