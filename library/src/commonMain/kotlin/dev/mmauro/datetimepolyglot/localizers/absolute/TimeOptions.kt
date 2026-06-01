package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.styles.DayPeriodStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle

/**
 * Options to pass when formatting time.
 *
 * @property styleOptions options defining which style to use
 * @property hourCycle if not null, overrides the hour cycle (H11, H12, H23, H24) to use. When `null`, uses the locale's default
 * @param SO the type of [TimeStyleOptions] to use, either [TimeStyleOptions.Local] or [TimeStyleOptions.Zoned].
 */
data class TimeOptions<out SO : TimeStyleOptions>(
    val styleOptions: SO,
    val hourCycle: HourCycle? = null,
)

/**
 * Options to pass to [TimeOptions.styleOptions], that define the style of each time component.
 *
 * @see TimeStyleOptions.Local
 * @see TimeStyleOptions.Zoned
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
 * @see TimeStyle.Local
 * @see TimeStyle.Zoned
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
 * @see TimeComponents.Local
 * @see TimeComponents.Zoned
 */
sealed interface TimeComponents : TimeStyleOptions {
    val hourStyle: HourStyle
    val minuteStyle: MinuteStyle?
    val secondStyle: SecondStyle?
    val fractionalSecondDigits: Int
    val dayPeriodStyle: DayPeriodStyle?

    data class Local(
        override val hourStyle: HourStyle,
        override val minuteStyle: MinuteStyle?,
        override val secondStyle: SecondStyle? = null,
        override val fractionalSecondDigits: Int = 0,
        override val dayPeriodStyle: DayPeriodStyle? = null,
    ) : TimeComponents, TimeStyleOptions.Local {
        init {
            require(fractionalSecondDigits in 0..3) { "fractionalSecondDigits must be in 0..3" }
        }
    }

    data class Zoned(
        override val hourStyle: HourStyle,
        override val minuteStyle: MinuteStyle?,
        override val secondStyle: SecondStyle? = null,
        override val fractionalSecondDigits: Int = 0,
        override val dayPeriodStyle: DayPeriodStyle? = null,
        val timeZoneStyle: TimeZoneStyle,
    ) : TimeComponents, TimeStyleOptions.Zoned {
        init {
            require(fractionalSecondDigits in 0..3) { "fractionalSecondDigits must be in 0..3" }
        }
    }
}



