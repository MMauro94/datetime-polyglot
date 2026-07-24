package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.ExperimentalZonedLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.localizers.PolyglotReferenceValueLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.ZonedYearLocalizer
import dev.mmauro.datetimepolyglot.localizers.absolute.ZonedYearOptions
import dev.mmauro.datetimepolyglot.localizers.localizeAsFlow
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeYearLocalizer
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeZonedYearLocalizer
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeZonedYearOptions
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/**
 * Localization options for [DynamicZonedYearLocalizer].
 *
 * @property relativeOptions options to use when the localization is relative
 * @property absoluteOptions options to use when the localization is absolute
 * @property relativeDiffRange configures the ranges of years difference with the reference point for which to use relative localization.
 * By default, this is `-1..+1`, meaning only last, current, and next year are localized relatively.
 */
@ExperimentalZonedLocalizer
data class DynamicZonedYearOptions(
    val relativeOptions: RelativeZonedYearOptions = RelativeZonedYearOptions(),
    val absoluteOptions: ZonedYearOptions = ZonedYearOptions(),
    val relativeDiffRange: IntRange = -1..1,
) : PolyglotLocalizerOptions<DynamicZonedYearLocalizer> {
    override fun localizer(locale: PlatformLocale) = DynamicZonedYearLocalizer(this, locale)
}

/**
 * Localizes a [Zoned] year ([Zoned]<[Int]>) dynamically (either absolute or relative to a [Zoned]<[Instant]> reference point).
 *
 * This behaves identically to [DynamicYearLocalizer], but it also adds a [TimeZone] in the mix (only in the absolute case).
 *
 * This class chooses between formatting with a [RelativeYearLocalizer] (if the difference is within the configured range), or falls back to
 * absolute formatting via [ZonedYearLocalizer]. Note that in case of relative localization, the relative value will always be calculated
 * using the reference's time zone and behave exactly the same as [RelativeYearLocalizer].
 *
 * As this class implements [PolyglotReferenceValueLocalizer], it allows to use [localizeAsFlow].
 *
 * Because kotlinx-datetime doesn't provide a standard type for a year, there is no extension function equivalent for one-off localizations.
 *
 * Examples:
 * - `last year`
 * - `1 year ago`
 * - `in 5y`
 * - `2026, Los Angeles Time`
 * - `2026 AD, Pacific Time`
 *
 * @see PolyglotReferenceValueLocalizer
 */
@ExperimentalZonedLocalizer
class DynamicZonedYearLocalizer(
    private val options: DynamicZonedYearOptions = DynamicZonedYearOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotReferenceValueLocalizer<Zoned<Int>> by InternalDynamicYearLocalizer(
    relativeLocalizer = RelativeZonedYearLocalizer(options.relativeOptions, locale),
    absoluteLocalizer = ZonedYearLocalizer(options.absoluteOptions, locale),
    relativeDiffRange = options.relativeDiffRange,
    yearProvider = { it.value },
)