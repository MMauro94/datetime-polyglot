package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.HourCycle
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

internal data class ComponentsOptions(
    val dateOptions: DateOptions? = null,
    val timeOptions: TimeOptions? = null,
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

        val hourCycle: HourCycle?

        data class Style(val style: TimeStyle, override val hourCycle: HourCycle? = null) : TimeOptions

        data class Components(
            val hourStyle: HourStyle? = null,
            val minuteStyle: MinuteStyle? = null,
            val secondStyle: SecondStyle? = null,
            val fractionalSecondDigits: Int = 0,
            val dayPeriodStyle: DayPeriodStyle? = null,
            val timeZoneStyle: TimeZoneStyle? = null,
            override val hourCycle: HourCycle? = null,
        ) : TimeOptions {
            init {
                require(fractionalSecondDigits in 0..3) { "fractionalSecondDigits must be in 0..3" }
                require(
                    hourStyle != null ||
                            minuteStyle != null ||
                            secondStyle != null ||
                            fractionalSecondDigits > 0 ||
                            timeZoneStyle != null
                ) {
                    "At least one component must be not-null"
                }
            }
        }
    }
}
