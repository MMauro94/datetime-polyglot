package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.styles.DayOfMonthStyle
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

data class LocalDateOptions(
    val eraStyle: EraStyle? = null,
    val yearStyle: YearStyle = YearStyle.NUMERIC_PADDED_4_DIGITS,
    val monthStyle: MonthStyle,
    val dayOfMonthStyle: DayOfMonthStyle? = null,
    val dayOfWeekStyle: DayOfWeekStyle? = null,
) {
    init {
        require(dayOfWeekStyle != null || dayOfMonthStyle != null) {
            "At least one between day of week and day of month styles need to be not null"
        }
    }
}

class LocalDateLocalizer(
    options: LocalDateOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<LocalDate> {

    private val componentsLocalizer = ComponentsLocalizer(
        locale = locale,
        options = ComponentsOptions(
            eraStyle = options.eraStyle,
            yearStyle = options.yearStyle,
            monthStyle = options.monthStyle,
            dayOfMonthStyle = options.dayOfMonthStyle,
            dayOfWeekStyle = options.dayOfWeekStyle,
        )
    )

    override fun localize(value: LocalDate): String {
        return componentsLocalizer.localize(Zoned(value.atStartOfDayIn(TimeZone.UTC), TimeZone.UTC))
    }
}

/**
 * Localizes this [LocalDate] with the given [options] in the given [locale].
 *
 * @see LocalDateLocalizer
 */
fun LocalDate.localize(
    options: LocalDateOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = LocalDateLocalizer(options, locale).localize(this)