package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.LOCALE_POLISH
import dev.mmauro.datetimepolyglot.TEST_PLATFORM
import dev.mmauro.datetimepolyglot.TestPlatform.Android
import dev.mmauro.datetimepolyglot.TestPlatform.Js
import dev.mmauro.datetimepolyglot.TestPlatform.Wasm
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.DayOfMonthStyle
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import dev.mmauro.datetimepolyglot.styles.TimeZoneStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withData
import io.kotest.datatest.withTests
import io.kotest.inspectors.forAny
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlin.time.Instant

private val ZONED_INSTANT = Zoned(
    Instant.parse("2026-06-19T19:25:45.123Z"),
    TimeZone.of("America/Los_Angeles")
)

val ZonedInstantTestFactory = funSpec {
    context("styles") {
        context("normal") {
            withContexts(nameFn = { "date style: $it" }, DateStyle.entries) { dateStyle ->
                withTests(nameFn = { "time style: $it" }, TimeStyle.Zoned.entries) { timeStyle ->
                    val expectedTime = when (timeStyle) {
                        TimeStyle.Zoned.LONG -> "12:25:45 PM PDT"
                        TimeStyle.Zoned.FULL -> "12:25:45 PM Pacific Daylight Time"
                    }
                    ZONED_INSTANT.localize(
                        options = ZonedInstantOptions(dateStyle, timeStyle),
                        locale = LOCALE_ENGLISH
                    ) shouldBeLocalizedAs when (dateStyle) {
                        DateStyle.SHORT -> "6/19/26, $expectedTime"
                        DateStyle.MEDIUM -> "Jun 19, 2026, $expectedTime"
                        DateStyle.LONG -> "June 19, 2026 at $expectedTime"
                        DateStyle.FULL -> "Friday, June 19, 2026 at $expectedTime"
                    }
                }
            }
        }

        context("with overridden hour cycle") {
            context("H24") {
                withTests(TimeStyle.Zoned.entries) { timeStyle ->
                    ZONED_INSTANT.localize(
                        options = ZonedInstantOptions(
                            dateOptions = DateStyle.SHORT,
                            timeOptions = TimeOptions(timeStyle, hourCycle = HourCycle.HOURS_24)
                        ),
                        locale = LOCALE_ENGLISH
                    ) shouldBeLocalizedAs when (timeStyle) {
                        TimeStyle.Zoned.LONG -> "6/19/26, 12:25:45 PDT"
                        TimeStyle.Zoned.FULL -> "6/19/26, 12:25:45 Pacific Daylight Time"
                    }
                }
            }
            context("H12") {
                withTests(TimeStyle.Zoned.entries) { timeStyle ->
                    ZONED_INSTANT.localize(
                        options = ZonedInstantOptions(
                            dateOptions = DateStyle.SHORT,
                            timeOptions = TimeOptions(timeStyle, hourCycle = HourCycle.HOURS_12),
                        ),
                        locale = LOCALE_ITALIAN
                    ) shouldBeLocalizedAs when (timeStyle) {
                        TimeStyle.Zoned.LONG -> "19/06/26, 12:25:45 PM GMT-7"
                        TimeStyle.Zoned.FULL -> "19/06/26, 12:25:45 PM Ora legale del Pacifico USA"
                    }
                }
            }
        }
    }

    context("components") {
        test("basic test") {
            ZONED_INSTANT.localize(
                options = ZonedInstantOptions(
                    dateOptions = DateComponents(
                        monthStyle = MonthStyle.ABBREVIATED,
                        dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
                        dayOfWeekStyle = DayOfWeekStyle.ABBREVIATED,
                    ),
                    timeOptions = TimeComponents.Zoned(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = MinuteStyle.NUMERIC,
                        timeZoneStyle = TimeZoneStyle.Generic.NON_LOCATION_LONG,
                    )
                ),
                locale = LOCALE_ENGLISH,
            ) shouldBeLocalizedAs "Fri, Jun 19, 2026, 12:25 PM Pacific Time"
        }

        dateComponentsTests { date, components, expectedDate ->
            val time = ZONED_INSTANT.toLocalDateTime().time
            val instant = date.atTime(time).toInstant(TimeZone.UTC)
            val localized = Zoned(instant, TimeZone.UTC).localize(
                options = ZonedInstantOptions(
                    dateOptions = components,
                    timeOptions = TimeComponents.Zoned(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = MinuteStyle.NUMERIC,
                        timeZoneStyle = TimeZoneStyle.Generic.ID,
                    )
                ),
                locale = LOCALE_ENGLISH,
            )
            listOf(", ", " at ").forAny { glue ->
                localized shouldBeLocalizedAs "$expectedDate${glue}12:25 PM UTC"
            }
        }

        timeComponentTests { time, options, testLocale, expectedTime ->
            val date = ZONED_INSTANT.toLocalDateTime().date
            val instant = date.atTime(time).toInstant(TimeZone.UTC)
            val localized = Zoned(instant, TimeZone.UTC).localize(
                options = ZonedInstantOptions(
                    dateOptions = DateComponents(
                        monthStyle = MonthStyle.ABBREVIATED,
                        dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
                    ),
                    timeOptions = options.toZoned(TimeZoneStyle.Generic.ID),
                ),
                locale = testLocale.locale,
            )
            val expectedDate = when (testLocale) {
                TimeComponentTestLocale.ENGLISH -> "Jun 19, 2026"
                TimeComponentTestLocale.ITALIAN -> "19 giu 2026"
            }
            // TimeZone could appear in the middle of the time, so we just remove it
            localized.replace(" UTC", "") shouldBeLocalizedAs "$expectedDate, $expectedTime"
        }

        fun optionsWithTimeZoneStyle(timeZoneStyle: TimeZoneStyle) = ZonedInstantOptions(
            dateOptions = DateComponents(
                yearStyle = YearStyle.NUMERIC,
                monthStyle = MonthStyle.ABBREVIATED,
                dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
            ),
            timeOptions = TimeComponents.Zoned(
                hourStyle = HourStyle.NUMERIC,
                minuteStyle = MinuteStyle.NUMERIC,
                timeZoneStyle = timeZoneStyle,
            )
        )

        context("time zone style") {
            fun TimeZoneStyle.runTest(expected: String) {
                val options = optionsWithTimeZoneStyle(this)
                ZONED_INSTANT.localize(options, LOCALE_ENGLISH) shouldBeLocalizedAs "Jun 19, 2026, 12:25 PM $expected"
            }

            context("generic") {
                withData(TimeZoneStyle.Generic.entries) { timeZoneStyle ->
                    val expected = when (timeZoneStyle) {
                        TimeZoneStyle.Generic.ID -> "America/Los_Angeles"
                        TimeZoneStyle.Generic.NON_LOCATION_SHORT -> "PT"
                        TimeZoneStyle.Generic.NON_LOCATION_LONG -> "Pacific Time"
                        TimeZoneStyle.Generic.LOCATION -> when (TEST_PLATFORM) {
                            // Web platforms don't support the "location" time zone style
                            is Js, is Wasm -> "Pacific Time"
                            else -> "Los Angeles Time"
                        }
                    }
                    timeZoneStyle.runTest(expected)
                }
            }
            context("specific") {
                withData(TimeZoneStyle.Specific.entries) { timeZoneStyle ->
                    val expected = when (timeZoneStyle) {
                        TimeZoneStyle.Specific.NON_LOCATION_SHORT -> "PDT"
                        TimeZoneStyle.Specific.NON_LOCATION_LONG -> "Pacific Daylight Time"
                    }
                    timeZoneStyle.runTest(expected)
                }
            }
            context("GMT") {
                withTests(TimeZoneStyle.Gmt.entries) { timeZoneStyle ->
                    val expected = when (timeZoneStyle) {
                        TimeZoneStyle.Gmt.SHORT -> "GMT-7"
                        TimeZoneStyle.Gmt.LONG -> "GMT-07:00"
                    }
                    timeZoneStyle.runTest(expected)
                }
            }
        }

        test("works in different language") {
            ZONED_INSTANT.localize(
                options = ZonedInstantOptions(
                    dateOptions = DateStyle.MEDIUM,
                    timeOptions = TimeStyle.Zoned.FULL,
                ),
                locale = LOCALE_ITALIAN
            ) shouldBeLocalizedAs "19 giu 2026, 12:25:45 Ora legale del Pacifico USA"
        }

        test("ensure it's not using standalone format for month") {
            ZONED_INSTANT.localize(
                options = ZonedInstantOptions(
                    dateOptions = DateStyle.LONG,
                    timeOptions = TimeStyle.Zoned.LONG,
                ),
                locale = LOCALE_POLISH
            ) shouldBeLocalizedAs "19 czerwca 2026 12:25:45 GMT-7"
        }

        context("mix-matching date/time style and components works") {
            context("date style with time components") {
                withTests(DateStyle.entries) { dateStyle ->
                    ZONED_INSTANT.localize(
                        options = ZonedInstantOptions(
                            dateOptions = dateStyle,
                            timeOptions = TimeComponents.Zoned(
                                hourStyle = HourStyle.NUMERIC,
                                minuteStyle = MinuteStyle.NUMERIC,
                                secondStyle = SecondStyle.NUMERIC,
                                timeZoneStyle = TimeZoneStyle.Gmt.LONG
                            ),
                        ),
                        locale = LOCALE_ENGLISH
                    ) shouldBeLocalizedAs when (dateStyle) {
                        DateStyle.SHORT -> "6/19/26, 12:25:45 PM GMT-07:00"
                        DateStyle.MEDIUM -> "Jun 19, 2026, 12:25:45 PM GMT-07:00"
                        DateStyle.LONG -> when (val platform = TEST_PLATFORM) {
                            is Android if platform.sdk <= 33 -> "June 19, 2026, 12:25:45 PM GMT-07:00"
                            else -> "June 19, 2026 at 12:25:45 PM GMT-07:00"
                        }

                        DateStyle.FULL -> when (val platform = TEST_PLATFORM) {
                            is Android if platform.sdk <= 33 -> "Friday, June 19, 2026, 12:25:45 PM GMT-07:00"
                            else -> "Friday, June 19, 2026 at 12:25:45 PM GMT-07:00"
                        }
                    }
                }
            }
            context("date components with time style") {
                withTests(MonthStyle.entries) { monthStyle ->
                    ZONED_INSTANT.localize(
                        options = ZonedInstantOptions(
                            dateOptions = DateComponents(
                                monthStyle = monthStyle,
                                dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
                            ),
                            timeOptions = TimeOptions(TimeStyle.Zoned.LONG),
                        ),
                        locale = LOCALE_ENGLISH
                    ) shouldBeLocalizedAs when (monthStyle) {
                        MonthStyle.NUMERIC -> "6/19/2026, 12:25:45 PM PDT"
                        MonthStyle.NUMERIC_PADDED_2_DIGITS -> "06/19/2026, 12:25:45 PM PDT"
                        MonthStyle.NARROW -> "J 19, 2026, 12:25:45 PM PDT"
                        MonthStyle.ABBREVIATED -> "Jun 19, 2026, 12:25:45 PM PDT"
                        MonthStyle.WIDE -> when (val platform = TEST_PLATFORM) {
                            is Android if platform.sdk <= 33 -> "June 19, 2026, 12:25:45 PM PDT"
                            else -> "June 19, 2026 at 12:25:45 PM PDT"
                        }
                    }
                }
            }
        }
    }
}

class ZonedInstantLocalizerTest : FunSpec({ include(ZonedInstantTestFactory) })
