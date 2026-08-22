package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.SYSTEM_CLOCK
import dev.mmauro.datetimepolyglot.SYSTEM_TIMEZONE
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.Defaults
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalTimeLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalTimeOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalTimeStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalTimeStyleOptions
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.localizeNow
import dev.mmauro.datetimepolyglot.map
import dev.mmauro.datetimepolyglot.utils.joinDateAndTime
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Localization options for [RelativeDateAbsoluteTimeLocalizer], [localizeRelativeDateAbsoluteTime], and
 * [localizeRelativeDateAbsoluteTimeAsFlow].
 *
 * @param dateOptions relative date options, see [RelativeLocalDateOptions]
 * @param timeOptions absolute time options, see [LocalTimeOptions]
 * @param joinerStyle the style of the join between the date and time components (e.g. `<date>, <time>` vs `<date> at <time>`)
 */
public data class RelativeDateAbsoluteTimeOptions(
    val dateOptions: RelativeLocalDateOptions = RelativeLocalDateOptions(),
    val timeOptions: LocalTimeOptions<*>,
    val joinerStyle: DateStyle = Defaults.JOINER,
) : PolyglotLocalizerOptions<RelativeDateAbsoluteTimeLocalizer> {
    public constructor(
        dateOptions: RelativeLocalDateOptions = RelativeLocalDateOptions(),
        timeOptions: LocalTimeStyleOptions = Defaults.LOCAL_TIME,
        joinerStyle: DateStyle = Defaults.JOINER,
    ) : this(dateOptions, LocalTimeOptions(timeOptions), joinerStyle)

    override fun localizer(locale: PlatformLocale): RelativeDateAbsoluteTimeLocalizer = RelativeDateAbsoluteTimeLocalizer(this, locale)
}

/**
 * Localizes the date part of a [LocalDateTime] relative to a [Zoned]<[Instant]> reference point, and the time part absolutely.
 *
 * This is mostly glue between [RelativeLocalDateLocalizer] and [LocalTimeLocalizer].
 * See their documentation to understand [RelativeDateAbsoluteTimeOptions.dateOptions] and [RelativeDateAbsoluteTimeOptions.timeOptions].
 *
 * Create once and re-use for localizing multiple values with the same [options].
 * Use [localizeRelativeDateAbsoluteTime] or [localizeRelativeDateAbsoluteTimeAsFlow] for one-off localizations.
 *
 * Examples:
 * - `yesterday at 9:00 PM`
 * - `next Sunday at 4:00 AM`
 * - `in 15 days, 3:00 AM`
 * - `21 days ago at 7:00 PM`
 */
public class RelativeDateAbsoluteTimeLocalizer(
    private val options: RelativeDateAbsoluteTimeOptions = RelativeDateAbsoluteTimeOptions(),
    private val locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceValueLocalizer<LocalDateTime> {

    private val relativeDateLocalizer = RelativeLocalDateLocalizer(options.dateOptions, locale)
    private val absoluteTimeLocalizer = LocalTimeLocalizer(options.timeOptions, locale)

    override fun localize(value: LocalDateTime, reference: Zoned<Instant>): TickingValue<String> {
        val dateValue = relativeDateLocalizer.localize(value.date, reference)
        val timeValue = absoluteTimeLocalizer.localize(value.time)

        return dateValue.map {
            joinDateAndTime(
                locale = locale,
                style = options.joinerStyle,
                date = it,
                time = timeValue,
            )
        }
    }
}

/**
 * Localizes this [LocalDateTime] relatively with respect to [reference], with a single unit with the given [options] in the given [locale].
 *
 * @see RelativeDateAbsoluteTimeLocalizer
 */
public fun LocalDateTime.localizeRelativeDateAbsoluteTime(
    reference: Zoned<Instant>,
    options: RelativeDateAbsoluteTimeOptions = RelativeDateAbsoluteTimeOptions(),
    locale: PlatformLocale = getDefaultLocale(),
): TickingValue<String> {
    return RelativeDateAbsoluteTimeLocalizer(options, locale).localize(this, reference)
}

/**
 * Localizes this [LocalDateTime] relatively with respect to [clock] @ [timeZone], with a single unit with the given [options] in the given
 * [locale].
 *
 * @see RelativeDateAbsoluteTimeLocalizer
 */
public fun LocalDateTime.localizeRelativeDateAbsoluteTimeNow(
    options: RelativeDateAbsoluteTimeOptions = RelativeDateAbsoluteTimeOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): TickingValue<String> {
    return RelativeDateAbsoluteTimeLocalizer(options, locale).localizeNow(this, clock, timeZone)
}

/**
 * Localizes this [LocalDateTime] relatively with respect to [clock] @ [timeZone], with a single unit with the given [options] in the given
 * [locale], returning a [Flow] that automatically receives new localizations as they are needed.
 *
 * @see RelativeDateAbsoluteTimeLocalizer
 * @see localizeAsFlow
 */
public fun LocalDateTime.localizeRelativeDateAbsoluteTimeAsFlow(
    options: RelativeDateAbsoluteTimeOptions = RelativeDateAbsoluteTimeOptions(),
    locale: PlatformLocale = getDefaultLocale(),
    clock: Flow<Clock> = SYSTEM_CLOCK,
    timeZone: Flow<TimeZone> = SYSTEM_TIMEZONE,
): Flow<String> {
    return RelativeDateAbsoluteTimeLocalizer(options, locale).localizeAsFlow(this, clock, timeZone)
}