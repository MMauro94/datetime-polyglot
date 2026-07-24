@file:OptIn(ExperimentalDynamicLocalizer::class)

package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.DateOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyleOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalDateLocalizer
import dev.mmauro.datetimepolyglot.localizers.localize
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeLocalDateLocalizer
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeLocalDateOptions
import dev.mmauro.datetimepolyglot.utils.map
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
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
data class DynamicLocalDateOptions private constructor(
    val relativeOptions: RelativeLocalDateOptions = RelativeLocalDateOptions(),
    val absoluteOptions: DateOptions,
    val relativeDiffRange: IntRange = DEFAULT_DIFF_RANGE,
) : PolyglotLocalizerOptions<DynamicLocalDateLocalizer> {

    constructor(
        relativeOptions: RelativeLocalDateOptions = RelativeLocalDateOptions(),
        absoluteOptions: DateStyleOptions,
        relativeDiffRange: IntRange = DEFAULT_DIFF_RANGE,
    ) : this(relativeOptions, DateOptions(absoluteOptions), relativeDiffRange)

    override fun localizer(locale: PlatformLocale) = DynamicLocalDateLocalizer(this, locale)

    companion object {
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
 * @see PolyglotReferenceValueLocalizer
 */
class DynamicLocalDateLocalizer(
    private val options: DynamicLocalDateOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceValueLocalizer<LocalDate> {

    private val relativeLocalDateLocalizer = RelativeLocalDateLocalizer(options.relativeOptions, locale)
    private val absoluteLocalDateLocalizer = LocalDateLocalizer(options.absoluteOptions, locale)

    override fun localize(value: LocalDate, reference: Zoned<Instant>): TickingValue<String> {
        val dynamicLocalizer = DynamicLocalizer(
            DynamicLocalizer.Case.Threshold(
                range = DynamicLocalizer.Case.Threshold.computeRangeFromDiff(
                    value = value,
                    diff = options.relativeDiffRange,
                    minus = { minus(it, DateTimeUnit.DAY) }
                ).map { it.atStartOfDayIn(reference.timeZone) },
                localizer = relativeLocalDateLocalizer,
            ),
            default = DynamicLocalizer.Case.Default(absoluteLocalDateLocalizer)
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
fun LocalDate.localizeDynamic(
    reference: Zoned<Instant>,
    options: DynamicLocalDateOptions,
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return DynamicLocalDateLocalizer(options, locale).localize(this, reference)
}

/**
 * Localizes this [LocalDate] dynamically (either absolute or relative to [clock]) with the given [options] in the given [locale].
 *
 * @see DynamicLocalDateLocalizer
 */
fun LocalDate.localizeDynamic(
    options: DynamicLocalDateOptions,
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
): TickingValue<String> {
    return DynamicLocalDateLocalizer(options, locale).localize(this, clock)
}

/**
 * Localizes this [LocalDate] dynamically (either absolute or relative to [clock]) with the given [options] in the given [locale], returning
 * a [Flow] that automatically receives new localizations as they are needed.
 *
 * @see DynamicLocalDateLocalizer
 * @see localizeAsFlow
 */
fun LocalDate.localizeDynamicAsFlow(
    options: DynamicLocalDateOptions,
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
): Flow<String> {
    return DynamicLocalDateLocalizer(options, locale).localizeAsFlow(this, clock)
}