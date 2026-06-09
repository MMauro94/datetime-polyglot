package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TestPlatform
import dev.mmauro.datetimepolyglot.noPlatforms
import dev.mmauro.datetimepolyglot.noWeb
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAsOneOf
import dev.mmauro.datetimepolyglot.styles.DayPeriodStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import kotlinx.datetime.LocalTime

private val TIME = LocalTime(hour = 21, minute = 5, second = 8, nanosecond = 123_999_999)

val LocalTimeLocalizerTestFactory = funSpec {
    context("styles") {
        context("normal") {
            withTests(TimeStyle.Local.entries) { timeStyle ->
                TIME.localize(timeStyle, LOCALE_ENGLISH) shouldBeLocalizedAs when (timeStyle) {
                    TimeStyle.Local.SHORT -> "9:05 PM"
                    TimeStyle.Local.MEDIUM -> "9:05:08 PM"
                }
            }
        }
        context("with overridden hour cycle") {
            context("H24") {
                withTests(TimeStyle.Local.entries) { timeStyle ->
                    TIME.localize(
                        options = TimeOptions(timeStyle, hourCycle = HourCycle.HOURS_24),
                        locale = LOCALE_ENGLISH
                    ) shouldBeLocalizedAs when (timeStyle) {
                        TimeStyle.Local.SHORT -> "21:05"
                        TimeStyle.Local.MEDIUM -> "21:05:08"
                    }
                }
            }
            context("H12") {
                withTests(TimeStyle.Local.entries) { timeStyle ->
                    TIME.localize(
                        options = TimeOptions(timeStyle, hourCycle = HourCycle.HOURS_12),
                        locale = LOCALE_ITALIAN
                    ) shouldBeLocalizedAs when (timeStyle) {
                        TimeStyle.Local.SHORT -> "9:05 PM"
                        TimeStyle.Local.MEDIUM -> "9:05:08 PM"
                    }
                }
            }
        }
    }

    context("components") {
        test("basic test") {
            TIME.localize(
                options = TimeComponents.Local(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = MinuteStyle.NUMERIC,
                ),
                locale = LOCALE_ENGLISH,
            ) shouldBeLocalizedAs "9:05 PM"
        }

        context("hour style") {
            fun HourStyle.test() {
                val components = TimeComponents.Local(
                    hourStyle = this,
                    minuteStyle = null,
                )
                TIME.localize(components, LOCALE_ENGLISH) shouldBeLocalizedAs when (this) {
                    HourStyle.NUMERIC -> "9 PM"
                    HourStyle.NUMERIC_PADDED_2_DIGITS -> "09 PM"
                }
            }

            withTests(HourStyle.entries - HourStyle.NUMERIC_PADDED_2_DIGITS) { hourStyle ->
                hourStyle.test()
            }

            test("NUMERIC_PADDED_2_DIGITS").config(
                enabledOrReasonIf = noPlatforms(
                    platforms = setOf(TestPlatform.JVM, TestPlatform.ANDROID, TestPlatform.JS_NODE),
                    reason = "This platform doesn't respect padding request for hour"
                ),
            ) {
                HourStyle.NUMERIC_PADDED_2_DIGITS.test()
            }
        }
        context("minute style") {
            withTests(MinuteStyle.entries) { minuteStyle ->
                val components = TimeComponents.Local(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = minuteStyle,
                )
                // Note: no-padding request is not respected in this case by any platform
                TIME.localize(components, LOCALE_ENGLISH) shouldBeLocalizedAs "9:05 PM"
            }
        }
        context("second style") {
            context("with minutes") {
                withTests(SecondStyle.entries) { secondStyle ->
                    val components = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = MinuteStyle.NUMERIC,
                        secondStyle = secondStyle,
                    )
                    // Note: no-padding request is not respected in this case by any platform
                    TIME.localize(components, LOCALE_ENGLISH) shouldBeLocalizedAs "9:05:08 PM"
                }
            }
            context("without minutes") {
                fun SecondStyle.test() {
                    val components = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = null,
                        secondStyle = this,
                    )
                    TIME.localize(components, LOCALE_ENGLISH) shouldBeLocalizedAs when (this) {
                        SecondStyle.NUMERIC -> "9 PM (second: 8)"
                        SecondStyle.NUMERIC_PADDED_2_DIGITS -> "9 PM (second: 08)"
                    }
                }

                withTests(SecondStyle.entries - SecondStyle.NUMERIC_PADDED_2_DIGITS) { secondStyle ->
                    secondStyle.test()
                }

                test("NUMERIC_PADDED_2_DIGITS").config(
                    enabledOrReasonIf = noPlatforms(
                        platforms = setOf(TestPlatform.JVM, TestPlatform.ANDROID, TestPlatform.JS_NODE),
                        reason = "This platform doesn't respect padding request for second in this case"
                    ),
                ) {
                    SecondStyle.NUMERIC_PADDED_2_DIGITS.test()
                }
            }
        }
        context("fractional second digits") {
            context("with minutes and seconds") {
                withTests(1..3) { fractionalSecondDigits ->
                    val components = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = MinuteStyle.NUMERIC,
                        secondStyle = SecondStyle.NUMERIC,
                        fractionalSecondDigits = fractionalSecondDigits,
                    )
                    TIME.localize(components, LOCALE_ENGLISH) shouldBeLocalizedAs when (fractionalSecondDigits) {
                        1 -> "9:05:08.1 PM"
                        2 -> "9:05:08.12 PM"
                        3 -> "9:05:08.123 PM"
                        else -> error("invalid test case")
                    }
                }
            }
            context("with no minute but with seconds").config(
                enabledOrReasonIf = noWeb("Web platforms do not care for fractional second digits if minute is undefined (may be a bug?)"),
            ) {
                withTests(1..3) { fractionalSecondDigits ->
                    val components = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = null,
                        secondStyle = SecondStyle.NUMERIC,
                        fractionalSecondDigits = fractionalSecondDigits,
                    )
                    TIME.localize(components, LOCALE_ENGLISH) shouldBeLocalizedAs when (fractionalSecondDigits) {
                        1 -> "9 PM (second: 8.1)"
                        2 -> "9 PM (second: 8.12)"
                        3 -> "9 PM (second: 8.123)"
                        else -> error("invalid test case")
                    }
                }
            }
            context("with no minute and seconds") {
                withTests(1..3) { fractionalSecondDigits ->
                    val components = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = null,
                        secondStyle = null,
                        fractionalSecondDigits = fractionalSecondDigits,
                    )
                    // Note: JS browser uses latter format, the rest of platforms use the first one
                    TIME.localize(components, LOCALE_ENGLISH) shouldBeLocalizedAsOneOf when (fractionalSecondDigits) {
                        1 -> listOf("9 PM ├F14: 1┤", "9 PM (Fractional Second: 1)")
                        2 -> listOf("9 PM ├F14: 12┤", "9 PM (Fractional Second: 12)")
                        3 -> listOf("9 PM ├F14: 123┤", "9 PM (Fractional Second: 123)")
                        else -> error("invalid test case")
                    }
                }
            }
        }
        context("day period style") {
            context("using a h24 locale has no effect") {
                withTests(DayPeriodStyle.entries) { dayPeriodStyle ->
                    val components = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = null,
                        dayPeriodStyle = dayPeriodStyle,
                    )
                    TIME.localize(components, LOCALE_ITALIAN) shouldBeLocalizedAs "21"
                }
            }
            context("using a h12 locale") {
                withTests(DayPeriodStyle.entries) { dayPeriodStyle ->
                    val components = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = null,
                        dayPeriodStyle = dayPeriodStyle,
                    )
                    // Note: in most languages, all three styles have the same value
                    TIME.localize(components, LOCALE_ENGLISH) shouldBeLocalizedAs "9 at night"
                }
            }
            context("using a h24 locale with overridden hour cycle") {
                withTests(DayPeriodStyle.entries) { dayPeriodStyle ->
                    val options = TimeOptions(
                        styleOptions = TimeComponents.Local(
                            hourStyle = HourStyle.NUMERIC,
                            minuteStyle = null,
                            dayPeriodStyle = dayPeriodStyle,
                        ),
                        hourCycle = HourCycle.HOURS_12,
                    )
                    // Note: in most languages, all three styles have the same value
                    TIME.localize(options, LOCALE_ITALIAN) shouldBeLocalizedAs "9 di sera"
                }
            }
            context("with other components") {
                withTests(DayPeriodStyle.entries) { dayPeriodStyle ->
                    val components = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = MinuteStyle.NUMERIC,
                        secondStyle = SecondStyle.NUMERIC,
                        dayPeriodStyle = dayPeriodStyle,
                    )
                    // Note: in most languages, all three styles have the same value
                    TIME.localize(components, LOCALE_ENGLISH) shouldBeLocalizedAs "9:05:08 at night"
                }
            }
        }
    }
}

class LocalTimeLocalizerTest : FunSpec({ include(LocalTimeLocalizerTestFactory) })