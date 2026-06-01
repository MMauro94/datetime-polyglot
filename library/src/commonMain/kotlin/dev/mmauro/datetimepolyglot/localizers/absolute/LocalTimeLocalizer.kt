package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import dev.mmauro.datetimepolyglot.localizers.absolute.ComponentsOptions.TimeOptions.*
import kotlinx.datetime.LocalTime

expect class LocalTimeLocalizer(
    options: TimeOptions<TimeStyleOptions.Local>,
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<LocalTime> {

    override fun localize(value: LocalTime): String
}

/**
 * Localizes this [LocalTime] with the given [options] in the given [locale].
 *
 * @see LocalTimeLocalizer
 */
fun LocalTime.localize(
    options: TimeOptions<TimeStyleOptions.Local>,
    locale: PlatformLocale = getDefaultLocale(),
) = LocalTimeLocalizer(options, locale).localize(this)

internal fun TimeOptions<*>.toComponentOptions(): ComponentsOptions.TimeOptions {
    return when (styleOptions) {
        is TimeStyle -> Style(styleOptions, hourCycle)
        is TimeComponents.Local -> Components(
            hourStyle = styleOptions.hourStyle,
            minuteStyle = styleOptions.minuteStyle,
            secondStyle = styleOptions.secondStyle,
            fractionalSecondDigits = styleOptions.fractionalSecondDigits,
            dayPeriodStyle = styleOptions.dayPeriodStyle,
            hourCycle = hourCycle,
        )

        is TimeComponents.Zoned -> Components(
            hourStyle = styleOptions.hourStyle,
            minuteStyle = styleOptions.minuteStyle,
            secondStyle = styleOptions.secondStyle,
            fractionalSecondDigits = styleOptions.fractionalSecondDigits,
            dayPeriodStyle = styleOptions.dayPeriodStyle,
            timeZoneStyle = styleOptions.timeZoneStyle,
            hourCycle = hourCycle,
        )
    }
}