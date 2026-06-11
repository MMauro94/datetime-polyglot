package dev.mmauro.datetimepolyglot.localizers.absolute

typealias LocalDateTimeOptions = DateTimeOptions<TimeStyleOptions.Local>
typealias ZonedDateTimeOptions = DateTimeOptions<TimeStyleOptions.Zoned>

data class DateTimeOptions<out TSO : TimeStyleOptions> internal constructor(
    val dateOptions: DateOptions,
    val timeOptions: TimeOptions<TSO>
) {

    companion object {
        // On JS, it's forbidden to mix styles and components, even when using e.g. date style and time components or vice versa
        // So for now we are disallowing creation of mix-match DateTimeOptions
        // This can be enabled on a per-platform version by adding a fake invoke constructor extension in the companion object

        // Local with styles
        operator fun invoke(dateOptions: DateStyle, timeOptions: TimeOptions<TimeStyle.Local>) =
            LocalDateTimeOptions(DateOptions(dateOptions), timeOptions)

        operator fun invoke(dateOptions: DateStyle, timeOptions: TimeStyle.Local) =
            LocalDateTimeOptions(DateOptions(dateOptions), TimeOptions(timeOptions))

        // Local with components
        operator fun invoke(dateOptions: DateComponents, timeOptions: TimeOptions<TimeComponents.Local>) =
            LocalDateTimeOptions(DateOptions(dateOptions), timeOptions)

        operator fun invoke(dateOptions: DateComponents, timeOptions: TimeComponents.Local) =
            LocalDateTimeOptions(DateOptions(dateOptions), TimeOptions(timeOptions))
    }
}