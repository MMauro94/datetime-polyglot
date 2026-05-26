package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.LOCALE_POLISH
import dev.mmauro.datetimepolyglot.TestPlatform
import dev.mmauro.datetimepolyglot.noJs
import dev.mmauro.datetimepolyglot.noNodeJs
import dev.mmauro.datetimepolyglot.noPlatforms
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.DayOfMonthStyle
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

private val DATE = LocalDate(2026, Month.JANUARY, 8)

val LocalDateLocalizerTestFactory = funSpec {
    test("basic test") {
        DATE.localize(
            options = LocalDateOptions(
                monthStyle = MonthStyle.NUMERIC_PADDED_2_DIGITS,
                dayOfMonthStyle = DayOfMonthStyle.NUMERIC_PADDED_2_DIGITS
            ),
            locale = LOCALE_ENGLISH,
        ) shouldBeLocalizedAs "01/08/2026"
    }

    context("era style") {
        withTests(EraStyle.entries) { eraStyle ->
            val options = LocalDateOptions(
                eraStyle = eraStyle,
                monthStyle = MonthStyle.NUMERIC_PADDED_2_DIGITS,
                dayOfMonthStyle = DayOfMonthStyle.NUMERIC_PADDED_2_DIGITS,
            )
            DATE.localize(options, LOCALE_ENGLISH) shouldBeLocalizedAs when (eraStyle) {
                EraStyle.NARROW -> "01/08/2026 A"
                EraStyle.ABBREVIATED -> "01/08/2026 AD"
                EraStyle.WIDE -> "01/08/2026 Anno Domini"
            }
        }
    }
    context("year style").config(
        enabledOrReasonIf = noPlatforms(
            platforms = setOf(TestPlatform.JS_NODE, TestPlatform.ANDROID),
            "NodeJS and Android have a bug formatting older dates: 01/01/123 gets formatted as Jan 2nd instead of Jan 1st"
        )
    ) {
        fun YearStyle.test() {
            val options = LocalDateOptions(
                yearStyle = this,
                monthStyle = MonthStyle.NUMERIC_PADDED_2_DIGITS,
                dayOfMonthStyle = DayOfMonthStyle.NUMERIC_PADDED_2_DIGITS,
            )
            LocalDate(123, Month.JANUARY, 1).localize(options, LOCALE_ENGLISH) shouldBeLocalizedAs when (this) {
                YearStyle.NUMERIC -> "01/01/123"
                YearStyle.NUMERIC_2_DIGITS -> "01/01/23"
                YearStyle.NUMERIC_PADDED_4_DIGITS -> "01/01/0123"
            }
        }

        withTests(YearStyle.entries - YearStyle.NUMERIC_PADDED_4_DIGITS) { yearStyle ->
            yearStyle.test()
        }

        test("NUMERIC_PADDED_4_DIGITS").config(
            enabledOrReasonIf = noJs("Web target doesn't support NUMERIC_PADDED_4_DIGITS"),
        ) {
            YearStyle.NUMERIC_PADDED_4_DIGITS.test()
        }
    }
    context("month style") {
        withTests(MonthStyle.entries) { monthStyle ->
            val options = LocalDateOptions(
                monthStyle = monthStyle,
                dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
            )
            DATE.localize(options, LOCALE_ENGLISH) shouldBeLocalizedAs when (monthStyle) {
                MonthStyle.NUMERIC -> "1/8/2026"
                MonthStyle.NUMERIC_PADDED_2_DIGITS -> "01/8/2026"
                MonthStyle.NARROW -> "J 8, 2026"
                MonthStyle.ABBREVIATED -> "Jan 8, 2026"
                MonthStyle.WIDE -> "January 8, 2026"
            }
        }
    }
    context("day of month style") {
        withTests(DayOfMonthStyle.entries) { dayOfMonthStyle ->
            val options = LocalDateOptions(
                monthStyle = MonthStyle.NUMERIC,
                dayOfMonthStyle = dayOfMonthStyle,
            )
            DATE.localize(options, LOCALE_ENGLISH) shouldBeLocalizedAs when (dayOfMonthStyle) {
                DayOfMonthStyle.NUMERIC -> "1/8/2026"
                DayOfMonthStyle.NUMERIC_PADDED_2_DIGITS -> "1/08/2026"
            }
        }
    }
    context("day of week style") {
        fun DayOfWeekStyle.test() {
            val options = LocalDateOptions(
                monthStyle = MonthStyle.WIDE,
                dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
                dayOfWeekStyle = this,
            )
            DATE.localize(options, LOCALE_ENGLISH) shouldBeLocalizedAs when (this) {
                DayOfWeekStyle.NARROW -> "T, January 8, 2026"
                DayOfWeekStyle.SHORT -> "Th, January 8, 2026"
                DayOfWeekStyle.ABBREVIATED -> "Thu, January 8, 2026"
                DayOfWeekStyle.WIDE -> "Thursday, January 8, 2026"
            }
        }

        withTests(DayOfWeekStyle.entries - DayOfWeekStyle.SHORT) { dayOfWeekStyle ->
            dayOfWeekStyle.test()
        }

        test("SHORT").config(
            enabledOrReasonIf = noJs("JS formats DayOfWeekStyle.SHORT differently (see DayOfWeekLocalizerTest)")
        ) {
            DayOfWeekStyle.SHORT.test()
        }
    }

    test("works in different language") {
        DATE.localize(
            options = LocalDateOptions(
                monthStyle = MonthStyle.NUMERIC_PADDED_2_DIGITS,
                dayOfMonthStyle = DayOfMonthStyle.NUMERIC_PADDED_2_DIGITS,
                dayOfWeekStyle = DayOfWeekStyle.WIDE,
            ),
            locale = LOCALE_ITALIAN
        ) shouldBeLocalizedAs "giovedì 08/01/2026"
    }

    test("ensure it's not using standalone format for month") {
        DATE.localize(
            options = LocalDateOptions(
                monthStyle = MonthStyle.WIDE,
                dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
            ),
            locale = LOCALE_POLISH
        ) shouldBeLocalizedAs "8 stycznia 2026"
    }

    // TODO understand day of week standalone vs format in polish/anohter language
}

class LocalDateLocalizerTest : FunSpec({ include(LocalDateLocalizerTestFactory) })