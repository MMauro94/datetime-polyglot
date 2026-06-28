package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.styles.DayPeriodStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle

/**
 * Options to pass when localizing time.
 *
 * @property styleOptions options defining the appearance of the localized time
 * @property hourCycle if not null, overrides the hour cycle (H11, H12, H23, H24) to use. When `null`, uses the locale's default
 * @param SO the type of [TimeStyleOptions] to use, either [TimeStyleOptions.Local] or [TimeStyleOptions.Zoned].
 */
data class TimeOptions<out SO : TimeStyleOptions>(
    val styleOptions: SO,
    val hourCycle: HourCycle? = null,
)

typealias LocalTimeOptions = TimeOptions<TimeStyleOptions.Local>
typealias ZonedTimeOptions = TimeOptions<TimeStyleOptions.Zoned>

/**
 * Options to pass to [TimeOptions.styleOptions], that define the style of each time component.
 *
 * @see Local
 * @see Zoned
 */
sealed interface TimeStyleOptions {

    /**
     * Options to pass to [TimeOptions.styleOptions] when localizing a time with no time zone awareness.
     *
     * @see TimeStyle.Local
     * @see TimeComponents.Local
     */
    sealed interface Local : TimeStyleOptions


    /**
     * Options to pass to [TimeOptions.styleOptions] when localizing a time with time zone awareness.
     *
     * @see TimeStyle.Zoned
     * @see TimeComponents.Zoned
     */
    sealed interface Zoned : TimeStyleOptions
}

/**
 * Defines a preset time style for localizing a time.
 *
 * @see Local
 * @see Zoned
 */
sealed interface TimeStyle : TimeStyleOptions {

    /**
     * Defines a preset time style for localizing a time with no time zone awareness
     */
    enum class Local : TimeStyle, TimeStyleOptions.Local {
        /**
         * Incudes hours and minutes (e.g. `21:05` or  `9:05 PM`)
         */
        SHORT,

        /**
         * Incudes hours, minutes and seconds (e.g. `21:05:08` or  `9:05:08 PM`)
         */
        MEDIUM,
    }

    /**
     * Defines a preset time style for localizing a time with time zone awareness
     */
    enum class Zoned : TimeStyle, TimeStyleOptions.Zoned {
        /**
         * Incudes hours, minutes and seconds and short timezone name (e.g. `21:05:08 CET` or  `9:05:08 PM CET`)
         */
        LONG,

        /**
         * Incudes hours, minutes and seconds and long timezone name (e.g. `21:05:08 European Central Time` or
         * `9:05:08 PM European Central Time`)
         */
        FULL,
    }
}

/**
 * Interface defining the style for each component of a time.
 *
 * @see Local
 * @see Zoned
 */
sealed interface TimeComponents : TimeStyleOptions {
    val hourStyle: HourStyle
    val minuteStyle: MinuteStyle?
    val secondStyle: SecondStyle?
    val fractionalSecondDigits: Int
    val dayPeriodStyle: DayPeriodStyle?

    /**
     * Interface defining the style for each component of a **local** time.
     *
     * @see TimeComponents
     */
    data class Local(
        override val hourStyle: HourStyle,
        override val minuteStyle: MinuteStyle?,
        override val secondStyle: SecondStyle? = null,
        override val fractionalSecondDigits: Int = 0,
        override val dayPeriodStyle: DayPeriodStyle? = null,
    ) : TimeComponents, TimeStyleOptions.Local, ComponentsOptions.TimeStyleOptions.Components {

        override val timeZoneStyle: Nothing? get() = null

        init {
            checkFractionalSecondDigits(fractionalSecondDigits)
        }
    }

    /**
     * Interface defining the style for each component of a **zoned** time.
     *
     * @see TimeComponents
     */
    data class Zoned(
        override val hourStyle: HourStyle,
        override val minuteStyle: MinuteStyle?,
        override val secondStyle: SecondStyle? = null,
        override val fractionalSecondDigits: Int = 0,
        override val dayPeriodStyle: DayPeriodStyle? = null,
        override val timeZoneStyle: TimeZoneStyle,
    ) : TimeComponents, TimeStyleOptions.Zoned, ComponentsOptions.TimeStyleOptions.Components {
        init {
            checkFractionalSecondDigits(fractionalSecondDigits)
        }
    }
}

private fun checkFractionalSecondDigits(fractionalSecondDigits: Int) {
    require(fractionalSecondDigits in 0..3) { "fractionalSecondDigits must be in 0..3" }
}

internal fun TimeOptions<*>.toComponentOptions(): ComponentsOptions.Time {
    return ComponentsOptions.Time(
        styleOptions = when (styleOptions) {
            is TimeStyle -> ComponentsOptions.TimeStyleOptions.Style(styleOptions)
            is TimeComponents.Local, is TimeComponents.Zoned -> styleOptions
        },
        hourCycle = hourCycle,
    )
}

internal fun TimeComponents.Local.toZoned(timeZoneStyle: TimeZoneStyle): TimeComponents.Zoned {
    return TimeComponents.Zoned(
        hourStyle = this.hourStyle,
        minuteStyle = this.minuteStyle,
        secondStyle = this.secondStyle,
        fractionalSecondDigits = this.fractionalSecondDigits,
        dayPeriodStyle = this.dayPeriodStyle,
        timeZoneStyle = timeZoneStyle,
    )
}

internal fun TimeOptions<TimeComponents.Local>.toZoned(timeZoneStyle: TimeZoneStyle): TimeOptions<TimeComponents.Zoned> {
    return TimeOptions(
        styleOptions = this.styleOptions.toZoned(timeZoneStyle),
        hourCycle = this.hourCycle,
    )
}