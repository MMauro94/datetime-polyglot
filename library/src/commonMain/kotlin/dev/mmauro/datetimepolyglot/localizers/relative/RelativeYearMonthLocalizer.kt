package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.localizeNow
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.plusMonth
import kotlinx.datetime.yearMonth
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Localization options for [RelativeYearMonthLocalizer], [YearMonth.localizeRelative], and [YearMonth.localizeRelativeAsFlow].
 *
 * @property style the style of the year
 * @property allowedDirections list of allowed relative directions to use for word style localization (e.g. `last month'). Pass an empty list
 * to force numeric localization (e.g. `1 month ago`). All directions are enabled by default.
 */
data class RelativeYearMonthOptions(
    override val style: RelativeUnitStyle = RelativeUnitStyle.LONG,
    override val allowedDirections: Set<RelativeDirection> = RelativeDirection.entries.toSet(),
) : RelativeUnitOptions, PolyglotLocalizerOptions<RelativeYearMonthLocalizer> {
    override fun localizer(locale: PlatformLocale) = RelativeYearMonthLocalizer(this, locale)
}

/**
 * Localizes a [YearMonth] relative to a [Zoned]<[Instant]> reference point.
 *
 * As this class implements [PolyglotReferenceValueLocalizer], it allows to use [localizeAsFlow].
 *
 * Localization could, based on [options], prioritize a word style localization (e.g. `last month`) over a numeric style (e.g.
 * `1 month ago`).
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [YearMonth.localizeRelative] or [YearMonth.localizeRelativeAsFlow] for one-off localizations.
 *
 * Examples:
 * - `this month`
 * - `last mo.`
 * - `next month`
 * - `4 months ago`
 * - `in 34mo.`
 *
 * @see PolyglotReferenceValueLocalizer
 */
class RelativeYearMonthLocalizer(
    private val options: RelativeYearMonthOptions = RelativeYearMonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceValueLocalizer<YearMonth> {

    private val relativeUnitLocalizer = RelativeUnitLocalizer(
        style = options.style,
        locale = locale,
    )

    /**
     * Convenience function to format an already calculated diff of months (e.g. -1 for `last month`).
     */
    fun localizeDiff(diff: Int): String {
        return relativeUnitLocalizer.localizeDiffDirection(diff.toDouble(), RelativeUnit.DateTimeComponent.MONTH, options.allowedDirections)
            ?: relativeUnitLocalizer.localizeNumeric(diff.toDouble(), RelativeUnit.DateTimeComponent.MONTH)
    }

    override fun localize(value: YearMonth, reference: Zoned<Instant>): TickingValue<String> {
        val referenceYearMonth = reference.toLocalDateTime().date.yearMonth
        return TickingValue(
            value = localizeDiff(referenceYearMonth.monthsUntil(value)),
            nextTick = referenceYearMonth.plusMonth().firstDay.atStartOfDayIn(reference.timeZone) - reference.value,
        )
    }
}

/**
 * Localizes this [YearMonth] relatively with respect to [reference], with a single unit with the given [options] in the given [locale].
 *
 * @see RelativeYearMonthLocalizer
 */
fun YearMonth.localizeRelative(
    reference: Zoned<Instant>,
    options: RelativeYearMonthOptions = RelativeYearMonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return RelativeYearMonthLocalizer(options, locale).localize(this, reference)
}

/**
 * Localizes this [YearMonth] relatively with respect to [clock] @ [timeZone], with a single unit with the given [options] in the given
 * [locale].
 *
 * @see RelativeYearMonthLocalizer
 */
fun YearMonth.localizeRelativeNow(
    options: RelativeYearMonthOptions = RelativeYearMonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): TickingValue<String> {
    return RelativeYearMonthLocalizer(options, locale).localizeNow(this, clock, timeZone)
}

/**
 * Localizes this [YearMonth] relatively with respect to [clock] @ [timeZone], with a single unit with the given [options] in the given
 * [locale], returning a [Flow] that automatically receives new localizations as they are needed.
 *
 * @see RelativeYearMonthLocalizer
 * @see localizeAsFlow
 */
fun YearMonth.localizeRelativeAsFlow(
    options: RelativeYearMonthOptions = RelativeYearMonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Flow<Clock> = SYSTEM_CLOCK,
    timeZone: Flow<TimeZone> = SYSTEM_TIMEZONE,
): Flow<String> {
    return RelativeYearMonthLocalizer(options, locale).localizeAsFlow(this, clock, timeZone)
}