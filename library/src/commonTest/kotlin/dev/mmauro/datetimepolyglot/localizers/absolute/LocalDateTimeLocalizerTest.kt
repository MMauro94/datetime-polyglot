package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.LOCALE_POLISH
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.DayOfMonthStyle
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withTests
import io.kotest.inspectors.forAny
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.atTime

private val DATE_TIME = LocalDateTime(2026, Month.JANUARY, 8, 21, 31, 45)

val LocalDateTimeLocalizerTestFactory = funSpec {
    context("styles") {
        context("normal") {
            withContexts(nameFn = { "date style: $it" }, DateStyle.entries) { dateStyle ->
                withTests(nameFn = { "time style: $it" }, TimeStyle.Local.entries) { timeStyle ->
                    val expectedTime = when (timeStyle) {
                        TimeStyle.Local.SHORT -> "9:31 PM"
                        TimeStyle.Local.MEDIUM -> "9:31:45 PM"
                    }
                    DATE_TIME.localize(
                        DateTimeOptions(dateStyle, timeStyle),
                        LOCALE_ENGLISH
                    ) shouldBeLocalizedAs when (dateStyle) {
                        DateStyle.SHORT -> "1/8/26, $expectedTime"
                        DateStyle.MEDIUM -> "Jan 8, 2026, $expectedTime"
                        DateStyle.LONG -> "January 8, 2026 at $expectedTime"
                        DateStyle.FULL -> "Thursday, January 8, 2026 at $expectedTime"
                    }
                }
            }
        }

        context("with overridden hour cycle") {
            context("H24") {
                withTests(TimeStyle.Local.entries) { timeStyle ->
                    DATE_TIME.localize(
                        options = DateTimeOptions(
                            dateOptions = DateStyle.SHORT,
                            timeOptions = TimeOptions(timeStyle, hourCycle = HourCycle.HOURS_24)
                        ),
                        locale = LOCALE_ENGLISH
                    ) shouldBeLocalizedAs when (timeStyle) {
                        TimeStyle.Local.SHORT -> "1/8/26, 21:31"
                        TimeStyle.Local.MEDIUM -> "1/8/26, 21:31:45"
                    }
                }
            }
            context("H12") {
                withTests(TimeStyle.Local.entries) { timeStyle ->
                    DATE_TIME.localize(
                        options = DateTimeOptions(
                            dateOptions = DateStyle.SHORT,
                            timeOptions = TimeOptions(timeStyle, hourCycle = HourCycle.HOURS_12),
                        ),
                        locale = LOCALE_ITALIAN
                    ) shouldBeLocalizedAs when (timeStyle) {
                        TimeStyle.Local.SHORT -> "08/01/26, 9:31 PM"
                        TimeStyle.Local.MEDIUM -> "08/01/26, 9:31:45 PM"
                    }
                }
            }
        }
    }

    context("components") {
        test("basic test") {
            DATE_TIME.localize(
                options = DateTimeOptions(
                    dateOptions = DateComponents(
                        monthStyle = MonthStyle.ABBREVIATED,
                        dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
                        dayOfWeekStyle = DayOfWeekStyle.ABBREVIATED,
                    ),
                    timeOptions = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = MinuteStyle.NUMERIC,
                    )
                ),
                locale = LOCALE_ENGLISH,
            ) shouldBeLocalizedAs "Thu, Jan 8, 2026, 9:31 PM"
        }

        dateComponentsTests { date, components, expectedDate ->
            val localized = date.atTime(DATE_TIME.time).localize(
                options = DateTimeOptions(
                    dateOptions = components,
                    timeOptions = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = MinuteStyle.NUMERIC,
                    )
                ),
                locale = LOCALE_ENGLISH,
            )
            listOf(", ", " at ").forAny { glue ->
                localized shouldBeLocalizedAs "$expectedDate${glue}9:31 PM"
            }
        }

        timeComponentTests { time, options, testLocale, expectedTime ->
            val localized = DATE_TIME.date.atTime(time).localize(
                options = DateTimeOptions(
                    dateOptions = DateComponents(
                        monthStyle = MonthStyle.ABBREVIATED,
                        dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
                    ),
                    timeOptions = options,
                ),
                locale = testLocale.locale,
            )
            val expectedDate = when (testLocale) {
                TimeComponentTestLocale.ENGLISH -> "Jan 8, 2026"
                TimeComponentTestLocale.ITALIAN -> "8 gen 2026"
            }
            localized shouldBeLocalizedAs "$expectedDate, $expectedTime"
        }

        test("works in different language") {
            DATE_TIME.localize(
                options = DateTimeOptions(
                    dateOptions = DateStyle.MEDIUM,
                    timeOptions = TimeStyle.Local.SHORT,
                ),
                locale = LOCALE_ITALIAN
            ) shouldBeLocalizedAs "8 gen 2026, 21:31"
        }

        test("ensure it's not using standalone format for month") {
            DATE_TIME.localize(
                options = DateTimeOptions(
                    dateOptions = DateStyle.LONG,
                    timeOptions = TimeStyle.Local.SHORT,
                ),
                locale = LOCALE_POLISH
            ) shouldBeLocalizedAs "8 stycznia 2026 21:31"
        }
    }
}

class LocalDateTimeLocalizerTest : FunSpec({ include(LocalDateTimeLocalizerTestFactory) })
