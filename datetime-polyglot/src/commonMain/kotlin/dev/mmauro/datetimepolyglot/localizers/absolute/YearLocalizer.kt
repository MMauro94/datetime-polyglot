package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle

/**
 * Localization options for [YearLocalizer].
 */
public data class YearOptions(
    override val eraStyle: EraStyle? = Defaults.ERA,
    override val yearStyle: YearStyle = Defaults.YEAR,
) : ComponentsOptions.Date.Components, PolyglotLocalizerOptions<YearLocalizer> {
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Implementation detail, shouldn't be used")
    override val monthStyle: Nothing? get() = null

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Implementation detail, shouldn't be used")
    override val dayOfMonthStyle: Nothing? get() = null

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Implementation detail, shouldn't be used")
    override val dayOfWeekStyle: Nothing? get() = null

    override fun localizer(locale: PlatformLocale): YearLocalizer = YearLocalizer(this, locale)
}

/**
 * Localizer for years ([Int]).
 *
 * Because kotlinx-datetime doesn't provide a standard type for a year, there is no extension function equivalent for one-off localizations.
 *
 * Examples:
 * - `2026`
 * - `26`
 * - `2026 AD`
 * - `2026 Anno Domini`
 */
public expect class YearLocalizer(
    options: YearOptions = YearOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeLocalizer<Int> {

    override fun localize(value: Int): String
}

// Note: as kotlinx-datetime doesn't offer a Year type, we don't offer an Int.localize() function that would be too broad and difficult
// to understand. Consumers will need to use YearLocalizer directly.
