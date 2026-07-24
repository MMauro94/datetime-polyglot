@file:OptIn(ExperimentalDynamicLocalizer::class)

package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.DateComponents
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalDateLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalDateTimeLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalDateTimeOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalTimeComponents
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalTimeOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalTimeStyle
import dev.mmauro.datetimepolyglot.localizers.localize
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeDateAbsoluteTimeLocalizer
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeDateAbsoluteTimeOptions
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeLocalDateOptions
import dev.mmauro.datetimepolyglot.utils.map
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlin.time.Clock
import kotlin.time.Instant


/**
 * Localization options for [DynamicDateAbsoluteTimeLocalizer].
 *
 * @property relativeOptions options to use when the localization is relative
 * @property absoluteOptions options to use when the localization is absolute
 * @property relativeDateDiffRange configures the ranges of days difference with the reference point for which to use relative localization.
 * By default, this is `-10..+10`, meaning that only the past and future 10 days are localized relatively.
 */
data class DynamicDateAbsoluteTimeOptions(
    val relativeOptions: RelativeDateAbsoluteTimeOptions = RelativeDateAbsoluteTimeOptions(),
    val absoluteOptions: LocalDateTimeOptions,
    val relativeDateDiffRange: IntRange = DynamicLocalDateOptions.DEFAULT_DIFF_RANGE,
) : PolyglotLocalizerOptions<DynamicDateAbsoluteTimeLocalizer> {

    constructor(
        relativeDateOptions: RelativeLocalDateOptions = RelativeLocalDateOptions(),
        absoluteDateStyle: DateStyle,
        timeOptions: LocalTimeOptions<LocalTimeStyle>,
        relativeJoinerStyle: DateStyle = DateStyle.LONG,
        relativeDateDiffRange: IntRange = DynamicLocalDateOptions.DEFAULT_DIFF_RANGE,
    ) : this(
        relativeOptions = RelativeDateAbsoluteTimeOptions(
            dateOptions = relativeDateOptions,
            timeOptions = timeOptions,
            joinerStyle = relativeJoinerStyle,
        ),
        absoluteOptions = LocalDateTimeOptions(
            dateOptions = absoluteDateStyle,
            timeOptions = timeOptions,
        ),
        relativeDateDiffRange = relativeDateDiffRange,
    )

    constructor(
        relativeDateOptions: RelativeLocalDateOptions = RelativeLocalDateOptions(),
        absoluteDateStyle: DateComponents,
        timeOptions: LocalTimeOptions<LocalTimeComponents>,
        joinerStyle: DateStyle = DateStyle.LONG,
        relativeDateDiffRange: IntRange = DynamicLocalDateOptions.DEFAULT_DIFF_RANGE,
    ) : this(
        relativeOptions = RelativeDateAbsoluteTimeOptions(
            dateOptions = relativeDateOptions,
            timeOptions = timeOptions,
            joinerStyle = joinerStyle,
        ),
        absoluteOptions = LocalDateTimeOptions(
            dateOptions = absoluteDateStyle,
            timeOptions = timeOptions,
        ),
        relativeDateDiffRange = relativeDateDiffRange,
    )

    override fun localizer(locale: PlatformLocale) = DynamicDateAbsoluteTimeLocalizer(this, locale)
}

/**
 * Localizes a [LocalDateTime] dynamically (either absolute or relative to a [Zoned]<[Instant]> reference point).
 *
 * This class chooses between formatting with a [RelativeDateAbsoluteTimeLocalizer] (if the difference is within the configured range), or
 * falls back to absolute formatting via [LocalDateLocalizer].
 *
 * So, in essence, the time is always localized absolutely, while the date is dynamic.
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [LocalDateTime.localizeDynamicDateAbsoluteTime] or [LocalDateTime.localizeDynamicDateAbsoluteTimeAsFlow] for one-off localizations.
 *
 * Examples:
 * - `yesterday at 9:00 AM`
 * - `1 day ago at 18:00`
 * - `next Sunday at 12:00 PM`
 * - `January 1 2026 at 8 in the morning`
 * - `1/1/26, 8 PM`
 *
 * @see PolyglotReferenceValueLocalizer
 */
class DynamicDateAbsoluteTimeLocalizer(
    private val options: DynamicDateAbsoluteTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceValueLocalizer<LocalDateTime> {

    private val relativeDateAbsoluteTimeLocalizer = RelativeDateAbsoluteTimeLocalizer(options.relativeOptions, locale)
    private val absoluteLocalDateTimeLocalizer = LocalDateTimeLocalizer(options.absoluteOptions, locale)

    override fun localize(value: LocalDateTime, reference: Zoned<Instant>): TickingValue<String> {
        val dynamicLocalizer = DynamicLocalizer(
            DynamicLocalizer.Case.Threshold(
                range = DynamicLocalizer.Case.Threshold.computeRangeFromDiff(
                    value = value.date,
                    diff = options.relativeDateDiffRange,
                    minus = { minus(it, DateTimeUnit.DAY) }
                ).map { it.atStartOfDayIn(reference.timeZone) },
                localizer = relativeDateAbsoluteTimeLocalizer,
            ),
            default = DynamicLocalizer.Case.Default(localizer = absoluteLocalDateTimeLocalizer),
        )

        return dynamicLocalizer.localize(value, reference)
    }
}

/**
 * Localizes this [LocalDateTime] dynamically (either absolute or relative to a [Zoned]<[Instant]> reference point) with the given [options] in
 * the given [locale].
 *
 * @see DynamicDateAbsoluteTimeLocalizer
 */
fun LocalDateTime.localizeDynamicDateAbsoluteTime(
    reference: Zoned<Instant>,
    options: DynamicDateAbsoluteTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return DynamicDateAbsoluteTimeLocalizer(options, locale).localize(this, reference)
}

/**
 * Localizes this [LocalDateTime] dynamically (either absolute or relative to [clock]) with the given [options] in the given [locale].
 *
 * @see DynamicDateAbsoluteTimeLocalizer
 */
fun LocalDateTime.localizeDynamicDateAbsoluteTime(
    options: DynamicDateAbsoluteTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
): TickingValue<String> {
    return DynamicDateAbsoluteTimeLocalizer(options, locale).localize(this, clock)
}

/**
 * Localizes this [LocalDateTime] dynamically (either absolute or relative to [clock]) with the given [options] in the given [locale], returning
 * a [Flow] that automatically receives new localizations as they are needed.
 *
 * @see DynamicDateAbsoluteTimeLocalizer
 * @see localizeAsFlow
 */
fun LocalDateTime.localizeDynamicDateAbsoluteTimeAsFlow(
    options: DynamicDateAbsoluteTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
): Flow<String> {
    return DynamicDateAbsoluteTimeLocalizer(options, locale).localizeAsFlow(this, clock)
}