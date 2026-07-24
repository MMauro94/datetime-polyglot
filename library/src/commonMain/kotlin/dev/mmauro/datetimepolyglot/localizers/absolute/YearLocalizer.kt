package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.localizers.PolyglotDateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle

/**
 * Localization options for [YearLocalizer].
 */
data class YearOptions(
    override val eraStyle: EraStyle? = null,
    override val yearStyle: YearStyle = YearStyle.NUMERIC,
) : ComponentsOptions.Date.Components {
    override val monthStyle: Nothing? get() = null
    override val dayOfMonthStyle: Nothing? get() = null
    override val dayOfWeekStyle: Nothing? get() = null
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
expect class YearLocalizer(
    options: YearOptions = YearOptions(),
    locale: PlatformLocale = getDefaultLocale(),
) : PolyglotDateTimeLocalizer<Int> {

    override fun localize(value: Int): String
}

// Note: as kotlinx-datetime doesn't offer a Year type, we don't offer an Int.localize() function that would be too broad and difficult
// to understand. Consumers will need to use YearLocalizer directly.
