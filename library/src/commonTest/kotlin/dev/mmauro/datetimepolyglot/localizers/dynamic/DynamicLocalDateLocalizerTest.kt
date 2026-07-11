package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.DEFAULT_INSTANT_RANGE
import dev.mmauro.datetimepolyglot.localizers.absolute.DateComponents
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.localDate
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeLocalDateOptions
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.days

private val NOW_DATE = LocalDateTime.parse("2026-06-01T00:00:00")
private val NOW = Zoned(NOW_DATE.toInstant(TimeZone.UTC), TimeZone.UTC)

val DynamicLocalDateLocalizerTestFactory = funSpec {
    context("localize") {
        context("dates over relative threshold are completely absolute") {
            withTests(
                nameFn = { Pair(it.b, it.c).toString() },
                tuple(
                    DynamicLocalDateLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = DynamicLocalDateOptions(
                            absoluteOptions = DateStyle.LONG
                        )
                    ),
                    LocalDate(2026, Month.JUNE, 20),
                    TickingValue("June 20, 2026", nextTick = 9.days),
                ),
                tuple(
                    DynamicLocalDateLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = DynamicLocalDateOptions(
                            absoluteOptions = DateComponents(monthStyle = MonthStyle.ABBREVIATED)
                        )
                    ),
                    LocalDate(2020, Month.APRIL, 14),
                    TickingValue("Apr 14, 2020", nextTick = null),
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
                DynamicLocalDateLocalizer(
                    locale = LOCALE_ENGLISH,
                    options = DynamicLocalDateOptions(
                        absoluteOptions = DateStyle.LONG,
                    ),
                ),
                LocalDate(2026, Month.JUNE, 1),
                TickingValue("today", nextTick = 1.days),
            ),
            tuple(
                DynamicLocalDateLocalizer(
                    locale = LOCALE_ENGLISH,
                    options = DynamicLocalDateOptions(
                        absoluteOptions = DateStyle.SHORT,
                        relativeOptions = RelativeLocalDateOptions(style = RelativeUnitStyle.SHORT)
                    ),
                ),
                LocalDate(2026, Month.MAY, 25),
                TickingValue("7 days ago", nextTick = 1.days),
            ),
        ) { (localizer, year, expected) ->
            localizer.localizeAndTestNextTick(year, NOW) shouldBe expected
        }
    }

    context("custom threshold") {
        val localizer = DynamicLocalDateLocalizer(
            locale = LOCALE_ITALIAN,
            options = DynamicLocalDateOptions(
                absoluteOptions = DateStyle.MEDIUM,
                relativeDiffRange = -1..15,
            )
        )

        withTests(
            nameFn = { it.first.toString() },
            LocalDate(2026, Month.MAY, 30) to "30 mag 2026",
            LocalDate(2026, Month.MAY, 31) to "ieri",
            LocalDate(2026, Month.JUNE, 1) to "oggi",
            LocalDate(2026, Month.JUNE, 16) to "tra 15 giorni",
            LocalDate(2026, Month.JUNE, 17) to "17 giu 2026",
        ) { (year, expected) ->
            localizer.localizeAndTestNextTick(year, NOW).value shouldBe expected
        }
    }

    context("nextTick") {
        val localizer = DynamicLocalDateLocalizer(
            locale = LOCALE_ENGLISH,
            options = DynamicLocalDateOptions(
                absoluteOptions = DateStyle.LONG,
            ),
        )

        localizer.nextTickPredictsChangeTest(
            arbitraryArb = Arb.localDate(),
            smallArb = { Arb.element(it.toLocalDateTime().date) },
            referenceRange = DEFAULT_INSTANT_RANGE,
        )
    }
}

class DynamicLocalDateLocalizerTest : FunSpec({
    include(DynamicLocalDateLocalizerTestFactory)
})
