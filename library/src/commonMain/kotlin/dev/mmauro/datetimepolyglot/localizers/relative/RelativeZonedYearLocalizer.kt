package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.ExperimentalZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.standalone.TimeZoneOptions
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/**
 * Localization options for [RelativeZonedYearLocalizer].
 *
 */
@ExperimentalZonedLocalizer
data class RelativeZonedYearOptions(
    val yearOptions: RelativeYearOptions = RelativeYearOptions(),
    val timeZoneOptions: TimeZoneOptions = TimeZoneOptions()
)

/**
 * Localizes a [Zoned] year ([Zoned]<[Int]) relative to a [Zoned]<[Instant]> reference point.
 *
 * This behaves identically to [RelativeYearLocalizer], but it also adds a [TimeZone] in the mix.
 * Note that the localized value is always relative to the reference time zone: the value's time zone is simply added in output, but it does
 * **not** affect relative calculations.
 *
 * Because a year represents a big span of time, the timezone could have different offsets at different moments within the year.
 * For this reason, only the [TimeZoneStyle.Generic] subset is allowed in the options for the time zone style.
 *
 * As this class implements [PolyglotReferenceValueLocalizer], it allows to use [localizeAsFlow].
 *
 * Because kotlinx-datetime doesn't provide a standard type for a year, there is no extension function equivalent for one-off localizations.
 *
 * Examples:
 * - `last yr., Los Angeles Time`
 * - `this year, PT`
 * - `1 year ago, Pacific Time`
 * - `in 5y, America/Los_Angeles`
 *
 * @see PolyglotReferenceValueLocalizer
 */
@ExperimentalZonedLocalizer
class RelativeZonedYearLocalizer(
    private val options: RelativeZonedYearOptions = RelativeZonedYearOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceValueLocalizer<Zoned<Int>> by RelativeZonedLocalizer(
    locale = locale,
    datePartLocalizer = RelativeYearLocalizer(options = options.yearOptions, locale = locale),
    timeZoneOptions = options.timeZoneOptions,
)