package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.DayOfMonthStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import io.kotest.core.spec.style.FunSpec
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month

private val DATE_TIME = LocalDateTime(2026, Month.JANUARY, 8, 21, 31, 45)

class LocalDateTimeLocalizerJvmAndAndroidTest : FunSpec({
    context("mix-matching date/time style and components works") {
        test("date style with time components") {
            DATE_TIME.localize(
                options = DateTimeOptions(
                    dateOptions = DateStyle.LONG,
                    timeOptions = TimeComponents.Local(
                        hourStyle = HourStyle.NUMERIC,
                        minuteStyle = MinuteStyle.NUMERIC,
                        secondStyle = SecondStyle.NUMERIC,
                    ),
                ),
                locale = LOCALE_ENGLISH
            ) shouldBeLocalizedAs "January 8, 2026 at 9:31:45 PM"
        }
        test("date components with time style") {
            DATE_TIME.localize(
                options = DateTimeOptions(
                    dateOptions = DateComponents(
                        monthStyle = MonthStyle.WIDE,
                        dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
                    ),
                    timeOptions = TimeOptions(TimeStyle.Local.SHORT),
                ),
                locale = LOCALE_ENGLISH
            ) shouldBeLocalizedAs "January 8, 2026 at 9:31 PM"
        }
    }
})