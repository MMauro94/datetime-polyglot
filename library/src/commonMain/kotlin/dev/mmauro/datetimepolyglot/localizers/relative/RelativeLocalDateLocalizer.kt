package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.firstDayOfWeek
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.localizeNow
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Localization options for [RelativeLocalDateLocalizer], [LocalDate.localizeRelative], and [LocalDate.localizeRelativeAsFlow].
 *
 * @property style the style of the date
 * @property allowedDirections list of allowed relative directions to use for word style localization (e.g. `yesterday'). Pass an empty list
 * to force numeric localization (e.g. `1 day ago`). All directions are enabled by default.
 * @property useRelativeDayOfWeek whether to allow using relative day of week formatting (e.g. `this Friday` or `next Monday`). Disabled by
 * default.
 */
public data class RelativeLocalDateOptions(
    override val style: RelativeUnitStyle = RelativeUnitStyle.LONG,
    override val allowedDirections: Set<RelativeDirection> = RelativeDirection.entries.toSet(),
    val useRelativeDayOfWeek: Boolean = false,
) : RelativeUnitOptions, PolyglotLocalizerOptions<RelativeLocalDateLocalizer> {
    override fun localizer(locale: PlatformLocale): RelativeLocalDateLocalizer = RelativeLocalDateLocalizer(this, locale)
}

/**
 * Localizes a [LocalDate] relative to a [Zoned]<[Instant]> reference point.
 *
 * As this class implements [PolyglotReferenceValueLocalizer], it allows to use [localizeAsFlow].
 *
 * Localization prioritizes, based on [options]:
 * - word style localization (e.g. `yesterday`, `today`, `tomorrow`, etc.)
 * - day of week relative localization (e.g. `this Friday` or `next Monday`)
 * - numeric style (e.g. `in 15 days`)
 *
 * Important note on day of week relative localization: given that this form of localization is sometimes ambiguous, it is bound be the
 * following rules:
 * - `last <day-of-week>` form is never used, because it could lead to ambiguity (e.g. if the reference is Wednesday, and the output is
 * `last Monday`, does it mean 2 days ago or 9 days ago?)
 * - `this <day-of-week>` is used when the day in question is in the future and falls in the same week (according to the [PlatformLocale])
 * - `next <day-of-week>` is used when the day in question is in the future, falls in the next week (according to the [PlatformLocale]), and
 * is within 7 days of the reference. This means that, `next <day-of-week>` can never be produced if the reference point is at a day of week
 * earlier than `<day-of-week>` (according to the [PlatformLocale]).
 * As a clarification example, imagine that we are using the `en-GB` locale (where the first day of the week is Monday), and the reference
 * is Wednesday. Only the following localizations can be produced using this format:
 *     - `this Thursday` (1 day in the future)
 *     - `this Friday` (2 days in the future)
 *     - `this Saturday` (3 days in the future)
 *     - `this Sunday` (4 days in the future)
 *     - `next Monday` (5 days in the future)
 *     - `next Tuesday` (6 days in the future)
 *     - `next Wednesday` (7 days in the future)
 *
 * Notice how in the example `next Thursday` cannot be produced because it would be ambiguous (does it mean in 1 day or in 8 days?). In this
 * case it would fall back to the regular numeric relative format (i.e. `in 8 days`).
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [LocalDate.localizeRelative] or [LocalDate.localizeRelativeAsFlow] for one-off localizations.
 *
 * Examples:
 * - `today`
 * - `yesteday`
 * - `tomorrow`
 * - `this Monday`
 * - `next Friday`
 * - `in 54 days`
 * - `5 days ago`
 *
 * @see PolyglotReferenceValueLocalizer
 */
public class RelativeLocalDateLocalizer(
    private val options: RelativeLocalDateOptions = RelativeLocalDateOptions(),
    private val locale: PlatformLocale = getDefaultLocale()
) : PolyglotReferenceValueLocalizer<LocalDate> {

    private val relativeUnitLocalizer = RelativeUnitLocalizer(
        style = options.style,
        locale = locale,
    )

    override fun localize(value: LocalDate, reference: Zoned<Instant>): TickingValue<String> {
        val referenceLocalDate = reference.toLocalDateTime().date
        val diff = referenceLocalDate.daysUntil(value)
        val nextDayTick = referenceLocalDate.plus(DatePeriod(days = 1)).atStartOfDayIn(reference.timeZone) - reference.value

        fun localizeDayOfWeekDirection(): TickingValue<String>? {
            if (!options.useRelativeDayOfWeek) {
                return null
            }

            if (diff in 1..DayOfWeek.entries.size) {
                val firstDoW = locale.firstDayOfWeek
                val nextFirstDoW = List(DayOfWeek.entries.size) { referenceLocalDate.plus(DatePeriod(days = it + 1)) }
                    .first { it.dayOfWeek == firstDoW }

                val direction = when {
                    value < nextFirstDoW -> RelativeDirection.THIS
                    else -> RelativeDirection.NEXT
                }

                return relativeUnitLocalizer.localizeDiffDirection(direction, RelativeUnit.DayOfWeek(value.dayOfWeek))?.let { localized ->
                    // Calculating a nextTick here is tricky because "this/next <day-of-week>" can be valid for multiple days, until either the
                    // direction changes (this becomes next) or a specific word is used (e.g. tomorrow), whose existence depends on the locale.
                    // Hence, we take a cheap shortcut here by checking the next day format to see if it would change
                    val nextDayFormat = localize(value, reference.copy(value = reference.value + nextDayTick))
                    val nextTick = if (nextDayFormat.value != localized) {
                        nextDayTick
                    } else {
                        nextDayTick + (nextDayFormat.nextTick ?: Duration.ZERO)
                    }
                    TickingValue(localized, nextTick = nextTick)
                }
            }
            return null
        }

        return relativeUnitLocalizer.localizeDiffDirection(diff.toDouble(), RelativeUnit.DateTimeComponent.DAY, options.allowedDirections)
            ?.let { TickingValue(it, nextDayTick) }
            ?: localizeDayOfWeekDirection()
            ?: TickingValue(relativeUnitLocalizer.localizeNumeric(diff.toDouble(), RelativeUnit.DateTimeComponent.DAY), nextDayTick)
    }
}


/**
 * Localizes this [LocalDate] relatively with respect to [reference], with a single unit with the given [options] in the given [locale].
 *
 * @see RelativeLocalDateLocalizer
 */
public fun LocalDate.localizeRelative(
    reference: Zoned<Instant>,
    options: RelativeLocalDateOptions = RelativeLocalDateOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return RelativeLocalDateLocalizer(options, locale).localize(this, reference)
}

/**
 * Localizes this [LocalDate] relatively with respect to [clock] @ [timeZone], with a single unit with the given [options] in the given
 * [locale].
 *
 * @see RelativeLocalDateLocalizer
 */
public fun LocalDate.localizeRelativeNow(
    options: RelativeLocalDateOptions = RelativeLocalDateOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): TickingValue<String> {
    return RelativeLocalDateLocalizer(options, locale).localizeNow(this, clock, timeZone)
}

/**
 * Localizes this [LocalDate] relatively with respect to [clock] @ [timeZone], with a single unit with the given [options] in the given
 * [locale], returning a [Flow] that automatically receives new localizations as they are needed.
 *
 * @see RelativeLocalDateLocalizer
 * @see localizeAsFlow
 */
public fun LocalDate.localizeRelativeAsFlow(
    options: RelativeLocalDateOptions = RelativeLocalDateOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Flow<Clock> = SYSTEM_CLOCK,
    timeZone: Flow<TimeZone> = SYSTEM_TIMEZONE,
): Flow<String> {
    return RelativeLocalDateLocalizer(options, locale).localizeAsFlow(this, clock, timeZone)
}