package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeZonedLocalizer
import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.styles.DayOfMonthStyle
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import dev.mmauro.datetimepolyglot.styles.DayPeriodStyle
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

internal data class ComponentsOptions(
    val eraStyle: EraStyle? = null,
    val yearStyle: YearStyle? = null,
    val monthStyle: MonthStyle? = null,
    val dayOfWeekStyle: DayOfWeekStyle? = null,
    val dayOfMonthStyle: DayOfMonthStyle? = null,
    val dayPeriodStyle: DayPeriodStyle? = null,
    val hourCycle: HourCycle? = null,
    val hourStyle: HourStyle? = null,
    val minuteStyle: MinuteStyle? = null,
    val secondStyle: SecondStyle? = null,
    val fractionalSecondDigits: Int = 0,
    val timezoneStyle: TimeZoneStyle? = null,
) {
    init {
        require(fractionalSecondDigits >= 0) { "fractionalSecondDigits cannot be negative" }
        require(
            eraStyle != null ||
                    yearStyle != null ||
                    monthStyle != null ||
                    dayOfWeekStyle != null ||
                    dayOfMonthStyle != null ||
                    hourStyle != null ||
                    minuteStyle != null ||
                    secondStyle != null ||
                    fractionalSecondDigits > 0 ||
                    timezoneStyle != null
        ) {
            "At least one component must be not-null"
        }
    }
}

/**
 * Class that is a localization primitive, allowing to format an [Instant] in a [TimeZone] by selecting the desired components to show.
 *
 * Note: actual output might vary slightly between platforms.
 *
 * Currently only meant to be used internally by other exposed localizers.
 */
internal expect class ComponentsLocalizer(
    locale: PlatformLocale = getDefaultLocale(),
    options: ComponentsOptions = ComponentsOptions(),
) : DateTimeZonedLocalizer<Instant> {
    override fun localize(value: Zoned<Instant>): String
}
