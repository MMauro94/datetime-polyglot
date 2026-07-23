@file:OptIn(ExperimentalDynamicLocalizer::class)

package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.YearLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.YearOptions
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeYearLocalizer
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeYearOptions
import dev.mmauro.datetimepolyglot.utils.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Instant

/**
 * Localization options for [DynamicYearLocalizer].
 *
 * @property relativeOptions options to use when the localization is relative
 * @property absoluteOptions options to use when the localization is absolute
 * @property relativeDiffRange configures the ranges of years difference with the reference point for which to use relative localization.
 * By default, this is `-1..+1`, meaning only last, current, and next year are localized relatively.
 */
data class DynamicYearOptions(
    val relativeOptions: RelativeYearOptions = RelativeYearOptions(),
    val absoluteOptions: YearOptions = YearOptions(),
    val relativeDiffRange: IntRange = -1..1,
)

/**
 * Localizes a year dynamically (either absolute or relative to a [Zoned]<[Instant]> reference point).
 *
 * This class chooses between formatting with a [RelativeYearLocalizer] (if the difference is within the configured range), or falls back to
 * absolute formatting via [YearLocalizer].
 *
 * As this class implements [PolyglotReferenceValueLocalizer], it allows to use [localizeAsFlow].
 *
 * Because kotlinx-datetime doesn't provide a standard type for a year, there is no extension function equivalent for one-off localizations.
 *
 * Examples:
 * - `last year`
 * - `1 year ago`
 * - `in 5y`
 * - `2026`
 * - `2026 AD`
 *
 * @see PolyglotReferenceValueLocalizer
 */
class DynamicYearLocalizer(
    options: DynamicYearOptions = DynamicYearOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceValueLocalizer<Int> by InternalDynamicYearLocalizer(
    relativeLocalizer = RelativeYearLocalizer(options.relativeOptions, locale),
    absoluteLocalizer = YearLocalizer(options.absoluteOptions, locale),
    relativeDiffRange = options.relativeDiffRange,
    yearProvider = { it },
)

internal class InternalDynamicYearLocalizer<T>(
    private val relativeLocalizer: PolyglotReferenceValueLocalizer<T>,
    private val absoluteLocalizer: PolyglotDateTimeLocalizer<T>,
    private val relativeDiffRange: IntRange,
    private val yearProvider: (T) -> Int,
) : PolyglotReferenceValueLocalizer<T> {

    override fun localize(value: T, reference: Zoned<Instant>): TickingValue<String> {
        val dynamicLocalizer = DynamicLocalizer(
            DynamicLocalizer.Case.Threshold(
                range = DynamicLocalizer.Case.Threshold.computeRangeFromDiff(
                    value = yearProvider(value),
                    diff = relativeDiffRange,
                    minus = Int::minus,
                ).map { LocalDate(year = it, Month.JANUARY, day = 1).atStartOfDayIn(reference.timeZone) },
                localizer = relativeLocalizer,
            ),
            default = DynamicLocalizer.Case.Default(absoluteLocalizer)
        )

        return dynamicLocalizer.localize(value, reference)
    }
}