package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.DateTimeLocalizer
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.getDefaultLocale
import kotlinx.datetime.LocalDateTime

data class DateTimeOptions internal constructor(
    val dateOptions: DateOptions,
    val timeOptions: LocalTimeOptions
) {
    // On JS, it's forbidden to mix styles and components, even when using e.g. date style and time components or vice versa
    // So for now we are disallowing creation of mix-match DateTimeOptions
    // This can be enabled on a per-platform version by adding a fake invoke constructor in the companion object

    // With styles
    constructor(dateOptions: DateStyle, timeOptions: TimeOptions<TimeStyle.Local>) : this(DateOptions(dateOptions), timeOptions)
    constructor(dateOptions: DateStyle, timeOptions: TimeStyle.Local) : this(DateOptions(dateOptions), TimeOptions(timeOptions))

    // With components
    constructor(dateOptions: DateComponents, timeOptions: TimeOptions<TimeComponents.Local>) : this(DateOptions(dateOptions), timeOptions)
    constructor(dateOptions: DateComponents, timeOptions: TimeComponents.Local) : this(DateOptions(dateOptions), TimeOptions(timeOptions))

    companion object
}

expect class LocalDateTimeLocalizer(
    options: DateTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
) : DateTimeLocalizer<LocalDateTime> {

    override fun localize(value: LocalDateTime): String
}

/**
 * Localizes this [LocalDateTime] with the given [options] in the given [locale].
 *
 * @see LocalDateTimeLocalizer
 */
fun LocalDateTime.localize(
    options: DateTimeOptions,
    locale: PlatformLocale = getDefaultLocale(),
) = LocalDateTimeLocalizer(options, locale).localize(this)
