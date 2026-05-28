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
    val dateOptions: DateOptions? = null,
    val timeOptions: TimeOptions? = null,
    val hourCycle: HourCycle? = null,
) {
    init {
        require(dateOptions != null || timeOptions != null) {
            "At least one option must be not-null"
        }
    }

    internal sealed interface DateOptions {

        data class Style(val style: DateStyle) : DateOptions

        data class Components(
            val eraStyle: EraStyle? = null,
            val yearStyle: YearStyle? = null,
            val monthStyle: MonthStyle? = null,
            val dayOfMonthStyle: DayOfMonthStyle? = null,
            val dayOfWeekStyle: DayOfWeekStyle? = null,
        ) : DateOptions {
            init {
                require(
                    eraStyle != null ||
                            yearStyle != null ||
                            monthStyle != null ||
                            dayOfMonthStyle != null ||
                            dayOfWeekStyle != null
                ) {
                    "At least one component must be not-null"
                }
            }
        }
    }

    internal sealed interface TimeOptions {

        data class Style(val style: TimeStyle) : TimeOptions

        data class Components(
            val dayPeriodStyle: DayPeriodStyle? = null,
            val hourStyle: HourStyle? = null,
            val minuteStyle: MinuteStyle? = null,
            val secondStyle: SecondStyle? = null,
            val fractionalSecondDigits: Int = 0,
            val timezoneStyle: TimeZoneStyle? = null,
        ) : TimeOptions {
            init {
                require(fractionalSecondDigits >= 0) { "fractionalSecondDigits cannot be negative" }
                require(
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
