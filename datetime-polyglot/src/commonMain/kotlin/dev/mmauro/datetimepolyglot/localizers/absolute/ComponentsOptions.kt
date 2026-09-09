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

/**
 * Simple internal-only class that allows to represent any set of possible styles for each component.
 *
 * This acts as a middle-man to create platform specific resources to localize values (e.g. see `ComponentsOptions.toDateFormat` in JVM and
 * `DateTimeFormatOptions.fill` functions in Web)
 */
internal data class ComponentsOptions(
    val dateOptions: Date? = null,
    val timeOptions: Time? = null,
) {
    init {
        require(dateOptions != null || timeOptions != null) {
            "At least one option must be not-null"
        }
    }

    internal sealed interface Date {

        data class Style(val style: DateStyle) : Date

        interface Components : Date {
            val eraStyle: EraStyle?
            val yearStyle: YearStyle?
            val monthStyle: MonthStyle?
            val dayOfMonthStyle: DayOfMonthStyle?
            val dayOfWeekStyle: DayOfWeekStyle?
        }
    }

    internal data class Time(
        val styleOptions: TimeStyleOptions,
        val hourCycle: HourCycle?,
    )

    internal sealed interface TimeStyleOptions {
        data class Style(val style: TimeStyle) : TimeStyleOptions

        interface Components : TimeStyleOptions {
            val hourStyle: HourStyle?
            val minuteStyle: MinuteStyle?
            val secondStyle: SecondStyle?
            val fractionalSecondDigits: Int
            val dayPeriodStyle: DayPeriodStyle?
            val timeZoneStyle: TimeZoneStyle?
        }
    }
}
