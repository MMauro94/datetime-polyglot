package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
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

internal fun TimeOptions<*>.toComponentOptions(): ComponentsOptions.Time {
    return ComponentsOptions.Time(
        styleOptions = when (styleOptions) {
            is TimeStyle -> ComponentsOptions.TimeStyleOptions.Style(styleOptions)
            is TimeComponents.Local, is TimeComponents.Zoned -> styleOptions
        },
        hourCycle = hourCycle,
    )
}