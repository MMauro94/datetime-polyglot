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
public sealed interface TimeOptions<out S : TimeStyleOptions> {

    /**
     * Options defining the appearance of the localized time
     */
    public val styleOptions: S

    /**
     * If not null, overrides the hour cycle (H11, H12, H23, H24) to use. When `null`, uses the locale's default
     */
    public val hourCycle: HourCycle?
}

/**
 * Options to pass when localizing a local time (no timezone information).
 */
public data class LocalTimeOptions<out S : LocalTimeStyleOptions>(
    override val styleOptions: S,
    override val hourCycle: HourCycle? = Defaults.HOUR_CYCLE
) : TimeOptions<S>, PolyglotLocalizerOptions<LocalTimeLocalizer> {
    override fun localizer(locale: PlatformLocale): LocalTimeLocalizer = LocalTimeLocalizer(this, locale)
}

/**
 * Options to pass when localizing a zoned time.
 */
public data class ZonedTimeOptions<out S : ZonedTimeStyleOptions>(
    override val styleOptions: S,
    override val hourCycle: HourCycle? = Defaults.HOUR_CYCLE,
) : TimeOptions<S>

/**
 * Options to pass to [TimeOptions.styleOptions], that define the style of each time component.
 *
 * @see LocalTimeStyleOptions
 * @see ZonedTimeStyleOptions
 */
public sealed interface TimeStyleOptions

/**
 * Options to pass to [TimeOptions.styleOptions] when localizing a time with no time zone awareness.
 *
 * @see LocalTimeStyle
 * @see LocalTimeComponents
 */
public sealed interface LocalTimeStyleOptions : TimeStyleOptions

/**
 * Options to pass to [TimeOptions.styleOptions] when localizing a time with time zone awareness.
 *
 * @see ZonedTimeStyle
 * @see ZonedTimeComponents
 */
public sealed interface ZonedTimeStyleOptions : TimeStyleOptions

/**
 * Defines a preset time style for localizing a time.
 *
 * @see LocalTimeStyle
 * @see ZonedTimeStyle
 */
public sealed interface TimeStyle : TimeStyleOptions

/**
 * Defines a preset time style for localizing a time with no time zone awareness
 */
public enum class LocalTimeStyle : TimeStyle, LocalTimeStyleOptions {
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
public enum class ZonedTimeStyle : TimeStyle, ZonedTimeStyleOptions {
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
public sealed interface TimeComponents : TimeStyleOptions {
    public val hourStyle: HourStyle
    public val minuteStyle: MinuteStyle?
    public val secondStyle: SecondStyle?
    public val fractionalSecondDigits: Int
    public val dayPeriodStyle: DayPeriodStyle?
}

/**
 * Interface defining the style for each component of a **local** time.
 *
 * @see TimeComponents
 */
public data class LocalTimeComponents(
        override val hourStyle: HourStyle = Defaults.HOUR,
        override val minuteStyle: MinuteStyle? = Defaults.MINUTE,
        override val secondStyle: SecondStyle? = Defaults.SECOND,
        override val fractionalSecondDigits: Int = Defaults.FRACTIONAL_SECONDS,
        override val dayPeriodStyle: DayPeriodStyle? = Defaults.DAY_PERIOD,
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
public data class ZonedTimeComponents(
    override val hourStyle: HourStyle = Defaults.HOUR,
    override val minuteStyle: MinuteStyle? = Defaults.MINUTE,
    override val secondStyle: SecondStyle? = Defaults.SECOND,
    override val fractionalSecondDigits: Int = Defaults.FRACTIONAL_SECONDS,
    override val dayPeriodStyle: DayPeriodStyle? = Defaults.DAY_PERIOD,
    override val timeZoneStyle: TimeZoneStyle = Defaults.TIME_ZONE,
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