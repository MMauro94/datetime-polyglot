package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.absolute.YearMonthOptions
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeYearMonthOptions
import dev.mmauro.datetimepolyglot.localizers.yearMonth
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.tuple
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.days

private val NOW_DATE = LocalDateTime.parse("2026-06-01T00:00:00")
private val NOW = Zoned(NOW_DATE.toInstant(TimeZone.UTC), TimeZone.UTC)

val DynamicYearMonthLocalizerTestFactory = funSpec {
    context("localize") {
        context("dates over relative threshold are completely absolute") {
            withTests(
                nameFn = { Pair(it.b, it.c).toString() },
                tuple(
                    DynamicYearMonthLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = DynamicYearMonthOptions(
                            absoluteOptions = YearMonthOptions(monthStyle = MonthStyle.ABBREVIATED)
                        )
                    ),
                    YearMonth(2026, Month.SEPTEMBER),
                    TickingValue("Sep 2026", nextTick = 61.days),
                ),
                tuple(
                    DynamicYearMonthLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = DynamicYearMonthOptions(
                            absoluteOptions = YearMonthOptions(monthStyle = MonthStyle.WIDE)
                        )
                    ),
                    YearMonth(2020, Month.APRIL),
                    TickingValue("April 2020", nextTick = null),
                ),
            ) { (localizer, yearMonth, expected) ->
                localizer.localizeAndTestNextTick(yearMonth, NOW) shouldBe expected
            }
        }
    }

    context("dates within relative threshold") {
        withTests(
            nameFn = { Pair(it.b, it.c).toString() },
            tuple(
                DynamicYearMonthLocalizer(
                    locale = LOCALE_ENGLISH,
                    options = DynamicYearMonthOptions(
                        absoluteOptions = YearMonthOptions(monthStyle = MonthStyle.WIDE)
                    ),
                ),
                YearMonth(2026, Month.JUNE),
                TickingValue("this month", nextTick = 30.days),
            ),
            tuple(
                DynamicYearMonthLocalizer(
                    locale = LOCALE_ENGLISH,
                    options = DynamicYearMonthOptions(
                        absoluteOptions = YearMonthOptions(monthStyle = MonthStyle.WIDE),
                        relativeOptions = RelativeYearMonthOptions(style = RelativeUnitStyle.SHORT)
                    ),
                ),
                YearMonth(2026, Month.MAY),
                TickingValue("last mo.", nextTick = 30.days),
            ),
        ) { (localizer, year, expected) ->
            localizer.localizeAndTestNextTick(year, NOW) shouldBe expected
        }
    }

    context("custom threshold") {
        val localizer = DynamicYearMonthLocalizer(
            locale = LOCALE_ITALIAN,
            options = DynamicYearMonthOptions(
                absoluteOptions = YearMonthOptions(monthStyle = MonthStyle.WIDE),
                relativeDiffRange = -10..5,
            )
        )

        withTests(
            nameFn = { it.first.toString() },
            YearMonth(2025, Month.JULY) to "luglio 2025",
            YearMonth(2025, Month.AUGUST) to "10 mesi fa",
            YearMonth(2026, Month.JUNE) to "questo mese",
            YearMonth(2026, Month.NOVEMBER) to "tra 5 mesi",
            YearMonth(2026, Month.DECEMBER) to "dicembre 2026",
        ) { (year, expected) ->
            localizer.localizeAndTestNextTick(year, NOW).value shouldBe expected
        }
    }

    context("nextTick") {
        val dateRange = LocalDateTime.parse("1950-01-01T00:00")..LocalDateTime.parse("2090-01-01T00:00:00")
        val localizer = DynamicYearMonthLocalizer(
            locale = LOCALE_ENGLISH,
            options = DynamicYearMonthOptions(
                absoluteOptions = YearMonthOptions(monthStyle = MonthStyle.WIDE),
            ),
        )

        localizer.nextTickPredictsChangeTest(
            arbitraryArb = Arb.yearMonth(
                min = YearMonth(1950, Month.JANUARY),
                max = YearMonth(2090, Month.DECEMBER),
            ),
            smallArb = { Arb.element(it.toLocalDateTime().let { YearMonth(it.year, it.month) }) },
            referenceRange = dateRange.start.toInstant(TimeZone.UTC)..dateRange.endInclusive.toInstant(TimeZone.UTC),
        )
    }
}

class DynamicYearMonthLocalizerTest : FunSpec({
    include(DynamicYearMonthLocalizerTestFactory)
})
