package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.PlatformLocale
import dev.mmauro.datetimepolyglot.TEST_PLATFORM
import dev.mmauro.datetimepolyglot.TestPlatform.Android
import dev.mmauro.datetimepolyglot.TestPlatform.Js
import dev.mmauro.datetimepolyglot.TestPlatform.Jvm
import dev.mmauro.datetimepolyglot.TestPlatform.Wasm
import dev.mmauro.datetimepolyglot.noPlatforms
import dev.mmauro.datetimepolyglot.noWeb
import dev.mmauro.datetimepolyglot.plus
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.DayPeriodStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.datatest.withTests
import kotlinx.datetime.LocalTime

private val TIME = LocalTime(hour = 21, minute = 5, second = 8, nanosecond = 123_999_999)

val LocalTimeLocalizerTestFactory = funSpec {
    context("styles") {
        context("normal") {
            withTests(LocalTimeStyle.entries) { timeStyle ->
                TIME.localize(timeStyle, LOCALE_ENGLISH) shouldBeLocalizedAs when (timeStyle) {
                    LocalTimeStyle.SHORT -> "9:05 PM"
                    LocalTimeStyle.MEDIUM -> "9:05:08 PM"
                }
            }
        }
        context("with overridden hour cycle") {
            context("H24") {
                withTests(LocalTimeStyle.entries) { timeStyle ->
                    TIME.localize(
                        options = LocalTimeOptions(timeStyle, hourCycle = HourCycle.HOURS_24),
                        locale = LOCALE_ENGLISH,
                    ) shouldBeLocalizedAs when (timeStyle) {
                        LocalTimeStyle.SHORT -> "21:05"
                        LocalTimeStyle.MEDIUM -> "21:05:08"
                    }
                }
            }
            context("H12") {
                withTests(LocalTimeStyle.entries) { timeStyle ->
                    TIME.localize(
                        options = LocalTimeOptions(timeStyle, hourCycle = HourCycle.HOURS_12),
                        locale = LOCALE_ITALIAN,
                    ) shouldBeLocalizedAs when (timeStyle) {
                        LocalTimeStyle.SHORT -> "9:05 PM"
                        LocalTimeStyle.MEDIUM -> "9:05:08 PM"
                    }
                }
            }
        }
    }

    context("components") {
        test("basic test") {
            TIME.localize(
                options = LocalTimeComponents(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = MinuteStyle.NUMERIC,
                ),
                locale = LOCALE_ENGLISH,
            ) shouldBeLocalizedAs "9:05 PM"
        }

        timeComponentTests { time, options, testLocale, expected ->
            time.localize(options, testLocale.locale) shouldBeLocalizedAs expected
        }
    }
}

enum class TimeComponentTestLocale(val locale: PlatformLocale) {
    ENGLISH(LOCALE_ENGLISH),
    ITALIAN(LOCALE_ITALIAN),
}

suspend fun FunSpecContainerScope.timeComponentTests(
    runTest: (time: LocalTime, options: LocalTimeOptions<LocalTimeComponents>, locale: TimeComponentTestLocale, expected: String) -> Unit,
) {
    context("hour style") {
        fun HourStyle.test() {
            val components = LocalTimeComponents(
                hourStyle = this,
                minuteStyle = null,
            )
            val expected = when (this) {
                HourStyle.NUMERIC -> "9 PM"
                HourStyle.NUMERIC_PADDED_2_DIGITS -> "09 PM"
            }
            runTest(TIME, LocalTimeOptions(components), TimeComponentTestLocale.ENGLISH, expected)
        }

        withTests(HourStyle.entries - HourStyle.NUMERIC_PADDED_2_DIGITS) { hourStyle ->
            hourStyle.test()
        }

        test("NUMERIC_PADDED_2_DIGITS").config(
            enabledOrReasonIf = noPlatforms(
                platforms = Jvm + Android + Js.Node,
                reason = "This platform doesn't respect padding request for hour",
            ),
        ) {
            HourStyle.NUMERIC_PADDED_2_DIGITS.test()
        }
    }
    context("minute style") {
        withTests(MinuteStyle.entries) { minuteStyle ->
            val components = LocalTimeComponents(
                hourStyle = HourStyle.NUMERIC,
                minuteStyle = minuteStyle,
            )
            // Note: no-padding request is not respected in this case by any platform
            runTest(TIME, LocalTimeOptions(components), TimeComponentTestLocale.ENGLISH, "9:05 PM")
        }
    }
    context("second style") {
        context("with minutes") {
            withTests(SecondStyle.entries) { secondStyle ->
                val components = LocalTimeComponents(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = MinuteStyle.NUMERIC,
                    secondStyle = secondStyle,
                )
                // Note: no-padding request is not respected in this case by any platform
                runTest(TIME, LocalTimeOptions(components), TimeComponentTestLocale.ENGLISH, "9:05:08 PM")
            }
        }
        context("without minutes") {
            fun SecondStyle.test() {
                val components = LocalTimeComponents(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = null,
                    secondStyle = this,
                )
                val expected = when (this) {
                    SecondStyle.NUMERIC -> "9 PM (second: 8)"
                    SecondStyle.NUMERIC_PADDED_2_DIGITS -> "9 PM (second: 08)"
                }
                runTest(TIME, LocalTimeOptions(components), TimeComponentTestLocale.ENGLISH, expected)
            }

            withTests(SecondStyle.entries - SecondStyle.NUMERIC_PADDED_2_DIGITS) { secondStyle ->
                secondStyle.test()
            }

            test("NUMERIC_PADDED_2_DIGITS").config(
                enabledOrReasonIf = noPlatforms(
                    platforms = Jvm + Android + Js.Node,
                    reason = "This platform doesn't respect padding request for second in this case",
                ),
            ) {
                SecondStyle.NUMERIC_PADDED_2_DIGITS.test()
            }
        }
    }
    context("fractional second digits") {
        context("with minutes and seconds") {
            withTests(1..3) { fractionalSecondDigits ->
                val components = LocalTimeComponents(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = MinuteStyle.NUMERIC,
                    secondStyle = SecondStyle.NUMERIC,
                    fractionalSecondDigits = fractionalSecondDigits,
                )
                val expected = when (fractionalSecondDigits) {
                    1 -> "9:05:08.1 PM"
                    2 -> "9:05:08.12 PM"
                    3 -> "9:05:08.123 PM"
                    else -> error("invalid test case")
                }
                runTest(TIME, LocalTimeOptions(components), TimeComponentTestLocale.ENGLISH, expected)
            }
        }
        context("with no minute but with seconds").config(
            enabledOrReasonIf = noWeb("Web platforms do not care for fractional second digits if minute is undefined (may be a bug?)"),
        ) {
            withTests(1..3) { fractionalSecondDigits ->
                val components = LocalTimeComponents(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = null,
                    secondStyle = SecondStyle.NUMERIC,
                    fractionalSecondDigits = fractionalSecondDigits,
                )
                val expected = when (fractionalSecondDigits) {
                    1 -> "9 PM (second: 8.1)"
                    2 -> "9 PM (second: 8.12)"
                    3 -> "9 PM (second: 8.123)"
                    else -> error("invalid test case")
                }
                runTest(TIME, LocalTimeOptions(components), TimeComponentTestLocale.ENGLISH, expected)
            }
        }
        context("with no minute and seconds") {
            withTests(1..3) { fractionalSecondDigits ->
                val components = LocalTimeComponents(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = null,
                    secondStyle = null,
                    fractionalSecondDigits = fractionalSecondDigits,
                )
                val expected = when (TEST_PLATFORM) {
                    is Js.Browser, is Wasm.Browser -> when (fractionalSecondDigits) {
                        1 -> "9 PM (Fractional Second: 1)"
                        2 -> "9 PM (Fractional Second: 12)"
                        3 -> "9 PM (Fractional Second: 123)"
                        else -> error("invalid test case")
                    }
                    else -> when (fractionalSecondDigits) {
                        1 -> "9 PM ├F14: 1┤"
                        2 -> "9 PM ├F14: 12┤"
                        3 -> "9 PM ├F14: 123┤"
                        else -> error("invalid test case")
                    }
                }
                runTest(TIME, LocalTimeOptions(components), TimeComponentTestLocale.ENGLISH, expected)
            }
        }
    }
    context("day period style") {
        context("using a h24 locale has no effect") {
            withTests(DayPeriodStyle.entries) { dayPeriodStyle ->
                val components = LocalTimeComponents(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = null,
                    dayPeriodStyle = dayPeriodStyle,
                )
                runTest(TIME, LocalTimeOptions(components), TimeComponentTestLocale.ITALIAN, "21")
            }
        }
        context("using a h12 locale") {
            withTests(DayPeriodStyle.entries) { dayPeriodStyle ->
                val components = LocalTimeComponents(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = null,
                    dayPeriodStyle = dayPeriodStyle,
                )
                // Note: in most languages, all three styles have the same value
                runTest(TIME, LocalTimeOptions(components), TimeComponentTestLocale.ENGLISH, "9 at night")
            }
        }
        context("using a h24 locale with overridden hour cycle") {
            withTests(DayPeriodStyle.entries) { dayPeriodStyle ->
                val options = LocalTimeOptions(
                    styleOptions = LocalTimeComponents(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = null,
                        dayPeriodStyle = dayPeriodStyle,
                    ),
                    hourCycle = HourCycle.HOURS_12,
                )
                // Note: in most languages, all three styles have the same value
                runTest(TIME, options, TimeComponentTestLocale.ITALIAN, "9 di sera")
            }
        }
        context("with other components") {
            withTests(DayPeriodStyle.entries) { dayPeriodStyle ->
                val components = LocalTimeComponents(
                    hourStyle = HourStyle.NUMERIC,
                    minuteStyle = MinuteStyle.NUMERIC,
                    secondStyle = SecondStyle.NUMERIC,
                    dayPeriodStyle = dayPeriodStyle,
                )
                // Note: in most languages, all three styles have the same value
                runTest(TIME, LocalTimeOptions(components), TimeComponentTestLocale.ENGLISH, "9:05:08 at night")
            }
        }
    }
}

class LocalTimeLocalizerTest : FunSpec({ include(LocalTimeLocalizerTestFactory) })
