package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle

data class YearOptions(
    override val eraStyle: EraStyle? = null,
    override val yearStyle: YearStyle = YearStyle.NUMERIC,
) : ComponentsOptions.Date.Components {
    override val monthStyle: Nothing? get() = null
    override val dayOfMonthStyle: Nothing? get() = null
    override val dayOfWeekStyle: Nothing? get() = null
}

expect class YearLocalizer(
    options: YearOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<Int> {

    override fun localize(value: Int): String
}

// Note: as kotlinx-datetime doesn't offer a Year type, we don't offer an Int.localize() function that would be too broad and difficult
// to understand. Consumers will need to use YearLocalizer directly.
