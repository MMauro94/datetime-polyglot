package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TEST_PLATFORM
import dev.mmauro.datetimepolyglot.TestPlatform.Android
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localeFromBcp47LanguageTag
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.localizers.yearMonth
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.tuple
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.yearMonth

private val REFERENCE_DATE = LocalDate.parse("2026-04-01")
private val REFERENCE = Zoned(REFERENCE_DATE.atStartOfDayIn(TimeZone.UTC), TimeZone.UTC)

val RelativeYearMonthLocalizerTestFactory = funSpec {
    context("localizer") {
        context("localizer string") {
            context("numeric") {
                withTests(
                    nameFn = { it.toString() },
                    tuple(LOCALE_ENGLISH, YearMonth(2023, Month.APRIL), "36 months ago"),
                    tuple(LOCALE_ENGLISH, YearMonth(2025, Month.OCTOBER), "6 months ago"),
                    tuple(LOCALE_ENGLISH, YearMonth(2026, Month.FEBRUARY), "2 months ago"),
                    tuple(LOCALE_ENGLISH, YearMonth(2026, Month.JUNE), "in 2 months"),
                    tuple(LOCALE_ENGLISH, YearMonth(2032, Month.JUNE), "in 74 months"),
                    tuple(LOCALE_ITALIAN, YearMonth(2023, Month.APRIL), "36 mesi fa"),
                    tuple(LOCALE_ITALIAN, YearMonth(2025, Month.OCTOBER), "6 mesi fa"),
                    tuple(LOCALE_ITALIAN, YearMonth(2026, Month.AUGUST), "tra 4 mesi"),
                ) { (locale, yearMonth, expected) ->
                    RelativeYearMonthLocalizer(locale = locale).localizeAndTestNextTick(yearMonth, REFERENCE).value shouldBe expected
                }
            }

            context("single word") {
                withTests(
                    nameFn = { it.toString() },
                    tuple(LOCALE_ENGLISH, YearMonth(2026, Month.MARCH), "last month"),
                    tuple(LOCALE_ENGLISH, YearMonth(2026, Month.APRIL), "this month"),
                    tuple(LOCALE_ENGLISH, YearMonth(2026, Month.MAY), "next month"),
                    tuple(LOCALE_ITALIAN, YearMonth(2026, Month.MARCH), "mese scorso"),
                    tuple(LOCALE_ITALIAN, YearMonth(2026, Month.APRIL), "questo mese"),
                    tuple(LOCALE_ITALIAN, YearMonth(2026, Month.MAY), "mese prossimo"),
                ) { (locale, yearMonth, expected) ->
                    RelativeYearMonthLocalizer(locale = locale).localizeAndTestNextTick(yearMonth, REFERENCE).value shouldBe expected
                }
            }

            context("number format follows locale convention") {
                withTests(
                    nameFn = { it.first.toString() },
                    LOCALE_ENGLISH to "in 1,000,000 months",
                    LOCALE_ITALIAN to "tra 1.000.000 mesi",
                    localeFromBcp47LanguageTag("hi") to "10,00,000 माह में",
                ) { (locale, expected) ->
                    val yearMonth = YearMonth(2026, Month.APRIL).plus(1_000_000, DateTimeUnit.MONTH)
                    RelativeYearMonthLocalizer(locale = locale).localizeAndTestNextTick(yearMonth, REFERENCE).value shouldBe expected
                }
            }

            context("styles") {
                context("numeric") {
                    withTests(RelativeUnitStyle.entries) { style ->
                        val localizer = RelativeYearMonthLocalizer(
                            locale = LOCALE_ENGLISH,
                            options = RelativeYearMonthOptions(style = style)
                        )
                        localizer.localizeAndTestNextTick(YearMonth(2026, Month.DECEMBER), REFERENCE).value shouldBe when (style) {
                            RelativeUnitStyle.NARROW -> when (val platform = TEST_PLATFORM) {
                                is Android if platform.sdk < 34 -> "in 8 mo."
                                else -> "in 8mo"
                            }

                            RelativeUnitStyle.SHORT -> "in 8 mo."
                            RelativeUnitStyle.LONG -> "in 8 months"
                        }
                    }
                }
                context("word") {
                    withTests(RelativeUnitStyle.entries) { style ->
                        val localizer = RelativeYearMonthLocalizer(
                            locale = LOCALE_ENGLISH,
                            options = RelativeYearMonthOptions(style = style)
                        )
                        localizer.localizeAndTestNextTick(YearMonth(2026, Month.MARCH), REFERENCE).value shouldBe when (style) {
                            RelativeUnitStyle.NARROW -> "last mo."
                            RelativeUnitStyle.SHORT -> "last mo."
                            RelativeUnitStyle.LONG -> "last month"
                        }
                    }
                }
            }
        }
    }

    test("localizeDiff") {
        RelativeYearMonthLocalizer(locale = LOCALE_ENGLISH).localizeDiff(-1) shouldBeLocalizedAs "last month"
    }

    context("next tick") {
        val localizer = RelativeYearMonthLocalizer(locale = LOCALE_ENGLISH)

        localizer.nextTickPredictsChangeTest(
            arbitraryArb = Arb.yearMonth(),
            smallArb = { Arb.element(it.toLocalDateTime().date.yearMonth) },
        )
    }
}

class RelativeYearMonthLocalizerTest : FunSpec({
    include(RelativeYearMonthLocalizerTestFactory)
})
