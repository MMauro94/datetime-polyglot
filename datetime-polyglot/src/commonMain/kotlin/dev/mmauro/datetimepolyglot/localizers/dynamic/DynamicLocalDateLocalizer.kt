@file:OptIn(ExperimentalDynamicLocalizer::class)

package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.ClockWrapper
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceDateTimeLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.DateOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyleOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.Defaults
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalDateLocalizer
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.localizeNow
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeLocalDateLocalizer
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeLocalDateOptions
import dev.mmauro.datetimepolyglot.utils.map
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Localization options for [DynamicLocalDateLocalizer].
 *
 * @property relativeOptions options to use when the localization is relative
 * @property absoluteOptions options to use when the localization is absolute
 * @property relativeDiffRange configures the ranges of days difference with the reference point for which to use relative localization.
 * By default, this is `-10..+10`, meaning that only the past and future 10 days are localized relatively.
 */
// For now this constructor is private because it's useless for users to manually create a DateOptions instance
public data class DynamicLocalDateOptions private constructor(
    val relativeOptions: RelativeLocalDateOptions = RelativeLocalDateOptions(),
    val absoluteOptions: DateOptions,
    val relativeDiffRange: IntRange = DEFAULT_DIFF_RANGE,
) : PolyglotLocalizerOptions<DynamicLocalDateLocalizer> {

    public constructor(
        relativeOptions: RelativeLocalDateOptions = RelativeLocalDateOptions(),
        absoluteOptions: DateStyleOptions = Defaults.DATE,
        relativeDiffRange: IntRange = DEFAULT_DIFF_RANGE,
    ) : this(relativeOptions, DateOptions(absoluteOptions), relativeDiffRange)

    override fun localizer(locale: PlatformLocale): DynamicLocalDateLocalizer = DynamicLocalDateLocalizer(this, locale)

    internal companion object {
        internal val DEFAULT_DIFF_RANGE = -10..10
    }
}

/**
 * Localizes a [LocalDate] dynamically (either absolute or relative to a [Zoned]<[Instant]> reference point).
 *
 * This class chooses between formatting with a [RelativeLocalDateLocalizer] (if the difference is within the configured range), or falls
 * back to absolute formatting via [LocalDateLocalizer].
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [LocalDate.localizeDynamic] or [LocalDate.localizeDynamicAsFlow] for one-off localizations.
 *
 * Examples:
 * - `yesterday`
 * - `1 day ago`
 * - `in 5 days`
 * - `01/01/2026`
 * - `January 1 2026`
 *
 * @see PolyglotReferenceDateTimeLocalizer
 */
public class DynamicLocalDateLocalizer(
    private val options: DynamicLocalDateOptions = DynamicLocalDateOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceDateTimeLocalizer<LocalDate> {

    private val relativeLocalDateLocalizer = RelativeLocalDateLocalizer(options.relativeOptions, locale)
    private val absoluteLocalDateLocalizer = LocalDateLocalizer(options.absoluteOptions, locale)

    override fun localize(value: LocalDate, reference: Zoned<Instant>): TickingValue<String> {
        val dynamicLocalizer = DynamicLocalizer(
            DynamicLocalizer.Case.Threshold(
                range = DynamicLocalizer.Case.Threshold.computeRangeFromDiff(
                    value = value,
                    diff = options.relativeDiffRange,
                    minus = { minus(it, DateTimeUnit.DAY) },
                ).map { it.atStartOfDayIn(reference.timeZone) },
                localizer = relativeLocalDateLocalizer,
            ),
            default = DynamicLocalizer.Case.Default(absoluteLocalDateLocalizer),
        )

        return dynamicLocalizer.localize(value, reference)
    }
}

/**
 * Localizes this [LocalDate] dynamically (either absolute or relative to a [Zoned]<[Instant]> reference point) with the given [options] in
 * the given [locale].
 *
 * @see DynamicLocalDateLocalizer
 */
public fun LocalDate.localizeDynamic(
    reference: Zoned<Instant>,
    options: DynamicLocalDateOptions = DynamicLocalDateOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return DynamicLocalDateLocalizer(options, locale).localize(this, reference)
}

/**
 * Localizes this [LocalDate] dynamically (either absolute or relative to [clock] @ [timeZone]) with the given [options] in the given
 * [locale].
 *
 * @see DynamicLocalDateLocalizer
 */
public fun LocalDate.localizeDynamicNow(
    options: DynamicLocalDateOptions = DynamicLocalDateOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): TickingValue<String> {
    return DynamicLocalDateLocalizer(options, locale).localizeNow(this, clock, timeZone)
}

/**
 * Localizes this [LocalDate] dynamically (either absolute or relative to [clock] @ [timeZone]) with the given [options] in the given
 * [locale], returning a [Flow] that automatically receives new localizations as they are needed.
 *
 * @see DynamicLocalDateLocalizer
 * @see localizeAsFlow
 */
public fun LocalDate.localizeDynamicAsFlow(
    options: DynamicLocalDateOptions = DynamicLocalDateOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Flow<ClockWrapper> = SYSTEM_CLOCK,
    timeZone: Flow<TimeZone> = SYSTEM_TIMEZONE,
): Flow<String> {
    return DynamicLocalDateLocalizer(options, locale).localizeAsFlow(this, clock, timeZone)
}
