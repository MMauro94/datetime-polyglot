package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Instant

/**
 * Localization options for [RelativeYearLocalizer].
 *
 * @property style the style of the year
 * @property allowedDirections list of allowed relative directions to use for word style localization (e.g. `last year'). Pass an empty list
 * to force numeric localization (e.g. `1 year ago`). All directions are enabled by default.
 */
data class RelativeYearOptions(
    override val style: RelativeUnitStyle = RelativeUnitStyle.LONG,
    override val allowedDirections: Set<RelativeDirection> = RelativeDirection.entries.toSet(),
) : RelativeUnitOptions

/**
 * Localizes a year relative to a [Zoned]<[Instant]> reference point.
 *
 * As this class implements [PolyglotReferenceValueLocalizer], it allows to use [localizeAsFlow].
 *
 * Localization could, based on [options], prioritize a word style localization (e.g. `last year`) over a numeric style (e.g. `1 year ago`).
 *
 * Examples:
 * - `last yr.`
 * - `this year`
 * - `next year`
 * - `1 year ago`
 * - `in 5y`
 *
 * @see PolyglotReferenceValueLocalizer
 */
class RelativeYearLocalizer(
    private val options: RelativeYearOptions = RelativeYearOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceValueLocalizer<Int> {

    private val relativeUnitLocalizer = RelativeUnitLocalizer(
        style = options.style,
        locale = locale,
    )

    /**
     * Convenience function to format an already calculated diff of years (e.g. -1 for `last year`).
     */
    fun localizeDiff(diff: Int): String {
        return relativeUnitLocalizer.localizeDiffDirection(diff.toDouble(), RelativeUnit.DateTimeComponent.YEAR, options.allowedDirections)
            ?: relativeUnitLocalizer.localizeNumeric(diff.toDouble(), RelativeUnit.DateTimeComponent.YEAR)
    }

    override fun localize(value: Int, reference: Zoned<Instant>): TickingValue<String> {
        val referenceYear = reference.toLocalDateTime().year
        return TickingValue(
            value = localizeDiff(value - referenceYear),
            nextTick = LocalDate(
                year = referenceYear + 1,
                month = Month.JANUARY,
                day = 1
            ).atStartOfDayIn(reference.timeZone) - reference.value,
        )
    }
}