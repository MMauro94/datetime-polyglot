package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.localizers.PolyglotLocalizerOptions
import dev.mmauro.datetimepolyglot.styles.DayPeriodStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle

/**
 * Options to pass when localizing time.
 */
sealed interface TimeOptions<out S : TimeStyleOptions> {

    /**
     * Options defining the appearance of the localized time
     */
    val styleOptions: S

    /**
     * If not null, overrides the hour cycle (H11, H12, H23, H24) to use. When `null`, uses the locale's default
     */
    val hourCycle: HourCycle?
}

/**
 * Options to pass when localizing a local time (no timezone information).
 */
data class LocalTimeOptions<out S : LocalTimeStyleOptions>(
    override val styleOptions: S,
    override val hourCycle: HourCycle? = null
) : TimeOptions<S>, PolyglotLocalizerOptions<LocalTimeLocalizer> {
    override fun localizer(locale: PlatformLocale) = LocalTimeLocalizer(this, locale)
}

/**
 * Options to pass when localizing a zoned time.
 */
data class ZonedTimeOptions<out S : ZonedTimeStyleOptions>(
    override val styleOptions: S,
    override val hourCycle: HourCycle? = null,
) : TimeOptions<S>

/**
 * Options to pass to [TimeOptions.styleOptions], that define the style of each time component.
 *
 * @see LocalTimeStyleOptions
 * @see ZonedTimeStyleOptions
 */
sealed interface TimeStyleOptions

/**
 * Options to pass to [TimeOptions.styleOptions] when localizing a time with no time zone awareness.
 *
 * @see LocalTimeStyle
 * @see LocalTimeComponents
 */
sealed interface LocalTimeStyleOptions : TimeStyleOptions

/**
 * Options to pass to [TimeOptions.styleOptions] when localizing a time with time zone awareness.
 *
 * @see ZonedTimeStyle
 * @see ZonedTimeComponents
 */
sealed interface ZonedTimeStyleOptions : TimeStyleOptions

/**
 * Defines a preset time style for localizing a time.
 *
 * @see LocalTimeStyle
 * @see ZonedTimeStyle
 */
sealed interface TimeStyle : TimeStyleOptions

/**
 * Defines a preset time style for localizing a time with no time zone awareness
 */
enum class LocalTimeStyle : TimeStyle, LocalTimeStyleOptions {
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
enum class ZonedTimeStyle : TimeStyle, ZonedTimeStyleOptions {
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

/**
 * Interface defining the style for each component of a time.
 *
 * @see LocalTimeComponents
 * @see ZonedTimeComponents
 */
sealed interface TimeComponents : TimeStyleOptions {
    val hourStyle: HourStyle
    val minuteStyle: MinuteStyle?
    val secondStyle: SecondStyle?
    val fractionalSecondDigits: Int
    val dayPeriodStyle: DayPeriodStyle?
}

/**
 * Interface defining the style for each component of a **local** time.
 *
 * @see TimeComponents
 */
data class LocalTimeComponents(
    override val hourStyle: HourStyle = HourStyle.NUMERIC,
    override val minuteStyle: MinuteStyle? = MinuteStyle.NUMERIC,
    override val secondStyle: SecondStyle? = null,
    override val fractionalSecondDigits: Int = 0,
    override val dayPeriodStyle: DayPeriodStyle? = null,
) : TimeComponents, LocalTimeStyleOptions, ComponentsOptions.TimeStyleOptions.Components {

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Implementation detail, shouldn't be used")
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
data class ZonedTimeComponents(
    override val hourStyle: HourStyle = HourStyle.NUMERIC,
    override val minuteStyle: MinuteStyle? = MinuteStyle.NUMERIC,
    override val secondStyle: SecondStyle? = null,
    override val fractionalSecondDigits: Int = 0,
    override val dayPeriodStyle: DayPeriodStyle? = null,
    override val timeZoneStyle: TimeZoneStyle,
) : TimeComponents, ZonedTimeStyleOptions, ComponentsOptions.TimeStyleOptions.Components {
    init {
        checkFractionalSecondDigits(fractionalSecondDigits)
    }
}

private fun checkFractionalSecondDigits(fractionalSecondDigits: Int) {
    require(fractionalSecondDigits in 0..3) { "fractionalSecondDigits must be in 0..3" }
}

internal fun TimeOptions<*>.toComponentOptions(): ComponentsOptions.Time {
    return ComponentsOptions.Time(
        styleOptions = when (val styleOptions = styleOptions) {
            is TimeStyle -> ComponentsOptions.TimeStyleOptions.Style(styleOptions)
            is LocalTimeComponents, is ZonedTimeComponents -> styleOptions
        },
        hourCycle = hourCycle,
    )
}

internal fun LocalTimeComponents.toZoned(timeZoneStyle: TimeZoneStyle): ZonedTimeComponents {
    return ZonedTimeComponents(
        hourStyle = this.hourStyle,
        minuteStyle = this.minuteStyle,
        secondStyle = this.secondStyle,
        fractionalSecondDigits = this.fractionalSecondDigits,
        dayPeriodStyle = this.dayPeriodStyle,
        timeZoneStyle = timeZoneStyle,
    )
}

internal fun LocalTimeOptions<LocalTimeComponents>.toZoned(timeZoneStyle: TimeZoneStyle): ZonedTimeOptions<ZonedTimeComponents> {
    return ZonedTimeOptions(
        styleOptions = this.styleOptions.toZoned(timeZoneStyle),
        hourCycle = this.hourCycle,
    )
}