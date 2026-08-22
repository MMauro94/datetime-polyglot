@file:OptIn(ExperimentalDynamicLocalizer::class)

package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.YearMonthLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.YearMonthOptions
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.localizeNow
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeYearMonthLocalizer
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeYearMonthOptions
import dev.mmauro.datetimepolyglot.utils.map
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Localization options for [DynamicYearMonthLocalizer].
 *
 * @property relativeOptions options to use when the localization is relative
 * @property absoluteOptions options to use when the localization is absolute
 * @property relativeDiffRange configures the ranges of months difference with the reference point for which to use relative localization.
 * By default, this is `-1..+1`, meaning only last, current, and next months are localized relatively.
 */
public data class DynamicYearMonthOptions(
    val relativeOptions: RelativeYearMonthOptions = RelativeYearMonthOptions(),
    val absoluteOptions: YearMonthOptions = YearMonthOptions(),
    val relativeDiffRange: IntRange = -1..1,
) : PolyglotLocalizerOptions<DynamicYearMonthLocalizer> {
    override fun localizer(locale: PlatformLocale): DynamicYearMonthLocalizer = DynamicYearMonthLocalizer(this, locale)
}

/**
 * Localizes a [YearMonth] dynamically (either absolute or relative to a [Zoned]<[Instant]> reference point).
 *
 * This class chooses between formatting with a [RelativeYearMonthLocalizer] (if the difference is within the configured range), or falls
 * back to absolute formatting via [YearMonthLocalizer].
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [YearMonth.localizeDynamic] or [YearMonth.localizeDynamicAsFlow] for one-off localizations.
 *
 *
 * Examples:
 * - `last month`
 * - `1 month ago`
 * - `in 5 mo`
 * - `07/2026`
 * - `July 2026`
 *
 * @see PolyglotReferenceValueLocalizer
 */
public class DynamicYearMonthLocalizer(
    private val options: DynamicYearMonthOptions = DynamicYearMonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceValueLocalizer<YearMonth> {

    private val relativeYearMonthLocalizer = RelativeYearMonthLocalizer(options.relativeOptions, locale)
    private val absoluteYearMonthLocalizer = YearMonthLocalizer(options.absoluteOptions, locale)

    override fun localize(value: YearMonth, reference: Zoned<Instant>): TickingValue<String> {
        val dynamicLocalizer = DynamicLocalizer(
            DynamicLocalizer.Case.Threshold(
                range = DynamicLocalizer.Case.Threshold.computeRangeFromDiff(
                    value = value,
                    diff = options.relativeDiffRange,
                    minus = { this.minus(it, DateTimeUnit.MONTH) },
                ).map { it.firstDay.atStartOfDayIn(reference.timeZone) },
                localizer = relativeYearMonthLocalizer,
            ),
            default = DynamicLocalizer.Case.Default(absoluteYearMonthLocalizer)
        )

        return dynamicLocalizer.localize(value, reference)
    }
}


/**
 * Localizes this [YearMonth] dynamically (either absolute or relative to a [Zoned]<[Instant]> reference point) with the given [options] in
 * the given [locale].
 *
 * @see DynamicYearMonthLocalizer
 */
public fun YearMonth.localizeDynamic(
    reference: Zoned<Instant>,
    options: DynamicYearMonthOptions = DynamicYearMonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return DynamicYearMonthLocalizer(options, locale).localize(this, reference)
}

/**
 * Localizes this [YearMonth] dynamically (either absolute or relative to [clock] @ [timeZone]) with the given [options] in the given
 * [locale].
 *
 * @see DynamicYearMonthLocalizer
 */
public fun YearMonth.localizeDynamicNow(
    options: DynamicYearMonthOptions = DynamicYearMonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): TickingValue<String> {
    return DynamicYearMonthLocalizer(options, locale).localizeNow(this, clock, timeZone)
}

/**
 * Localizes this [YearMonth] dynamically (either absolute or relative to [clock] @ [timeZone]) with the given [options] in the given
 * [locale], returning a [Flow] that automatically receives new localizations as they are needed.
 *
 * @see DynamicYearMonthLocalizer
 * @see localizeAsFlow
 */
public fun YearMonth.localizeDynamicAsFlow(
    options: DynamicYearMonthOptions = DynamicYearMonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Flow<Clock> = SYSTEM_CLOCK,
    timeZone: Flow<TimeZone> = SYSTEM_TIMEZONE,
): Flow<String> {
    return DynamicYearMonthLocalizer(options, locale).localizeAsFlow(this, clock, timeZone)
}