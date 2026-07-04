package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.localize
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.monthsUntil
import kotlinx.datetime.plusMonth
import kotlinx.datetime.yearMonth
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Localization options for [RelativeYearMonthLocalizer].
 *
 * @property style the style of the year
 * @property allowedDirections list of allowed relative directions to use for word style localization (e.g. `last month'). Pass an empty list
 * to force numeric localization (e.g. `1 month ago`)
 */
data class RelativeYearMonthOptions(
    override val style: RelativeUnitStyle = RelativeUnitStyle.LONG,
    override val allowedDirections: List<RelativeDirection> = RelativeDirection.entries,
) : RelativeUnitOptions

/**
 * Localizes a [YearMonth] relative to a [Zoned]<[Instant]> reference point.
 *
 * As this class implements [PolyglotReferenceValueLocalizer], it allows to use [localizeAsFlow].
 *
 * Localization could, based on [options], prioritize a word style localization (e.g. `last month`) over a numeric style (e.g.
 * `1 month ago`).
 *
 * Examples:
 * - `this month`
 * - `last month`
 * - `next month`
 * - `4 months ago`
 * - `in 34 months`
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
        return relativeUnitLocalizer.localize(diff.toDouble(), RelativeUnit.DateTimeComponent.MONTH, options.allowedDirections)
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
 * Localizes this [YearMonth] relatively with respect to [clock], with a single unit with the given [options] in the given [locale].
 *
 * @see RelativeYearMonthLocalizer
 */
fun YearMonth.localizeRelative(
    options: RelativeYearMonthOptions = RelativeYearMonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
): TickingValue<String> {
    return RelativeYearMonthLocalizer(options, locale).localize(this, clock)
}

/**
 * Localizes this [YearMonth] relatively with respect to [clock], with a single unit with the given [options] in the given [locale],
 * returning a [Flow].
 *
 * @see RelativeYearMonthLocalizer
 * @see localizeAsFlow
 */
fun YearMonth.localizeRelativeAsFlow(
    options: RelativeYearMonthOptions = RelativeYearMonthOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
): Flow<String> {
    return RelativeYearMonthLocalizer(options, locale).localizeAsFlow(this, clock)
}