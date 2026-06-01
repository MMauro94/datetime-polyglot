package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.TestPlatform
import dev.mmauro.datetimepolyglot.noPlatforms
import dev.mmauro.datetimepolyglot.noWeb
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import kotlinx.datetime.Month
import kotlinx.datetime.YearMonth

private val YEAR_MONTH = YearMonth(2026, Month.AUGUST)

val YearMonthLocalizerTestFactory = funSpec {
    context("year style") {
        fun YearStyle.test() {
            val options = YearMonthOptions(
                yearStyle = this,
                monthStyle = MonthStyle.WIDE,
            )
            YearMonth(123, Month.MARCH).localize(options, LOCALE_ENGLISH) shouldBeLocalizedAs when (this) {
                YearStyle.NUMERIC -> "March 123"
                YearStyle.NUMERIC_2_DIGITS -> "March 23"
                YearStyle.NUMERIC_PADDED_4_DIGITS -> "March 0123"
            }
        }

        withTests(YearStyle.entries - YearStyle.NUMERIC_PADDED_4_DIGITS) { yearStyle ->
            yearStyle.test()
        }

        test("NUMERIC_PADDED_4_DIGITS").config(
            enabledOrReasonIf = noWeb("This platform doesn't support padding request for year"),
        ) {
            YearStyle.NUMERIC_PADDED_4_DIGITS.test()
        }
    }
    context("era style") {
        withTests(EraStyle.entries) { eraStyle ->
            val options = YearMonthOptions(
                eraStyle = eraStyle,
                yearStyle = YearStyle.NUMERIC,
                monthStyle = MonthStyle.WIDE,
            )
            YEAR_MONTH.localize(options, LOCALE_ENGLISH) shouldBeLocalizedAs when (eraStyle) {
                EraStyle.NARROW -> "August 2026 A"
                EraStyle.ABBREVIATED -> "August 2026 AD"
                EraStyle.WIDE -> "August 2026 Anno Domini"
            }
        }
    }
    context("month style") {
        withTests(MonthStyle.entries) { monthStyle ->
            val options = YearMonthOptions(
                yearStyle = YearStyle.NUMERIC,
                monthStyle = monthStyle,
            )
            YEAR_MONTH.localize(options, LOCALE_ENGLISH) shouldBeLocalizedAs when (monthStyle) {
                MonthStyle.NUMERIC -> "8/2026"
                MonthStyle.NUMERIC_PADDED_2_DIGITS -> "08/2026"
                MonthStyle.NARROW -> "A 2026"
                MonthStyle.ABBREVIATED -> "Aug 2026"
                MonthStyle.WIDE -> "August 2026"
            }
        }
    }
}

class YearMonthLocalizerTest : FunSpec({ include(YearMonthLocalizerTestFactory) })