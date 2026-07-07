package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.absolute.YearOptions
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeYearOptions
import dev.mmauro.datetimepolyglot.localizers.year
import dev.mmauro.datetimepolyglot.styles.EraStyle
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.days

private val NOW_DATE = LocalDateTime.parse("2026-06-01T00:00:00")
private val NOW = Zoned(NOW_DATE.toInstant(TimeZone.UTC), TimeZone.UTC)

val DynamicYearLocalizerTestFactory = funSpec {
    context("localize") {
        context("dates over relative threshold are completely absolute") {
            withTests(
                nameFn = { Pair(it.b, it.c).toString() },
                tuple(
                    DynamicYearLocalizer(locale = LOCALE_ENGLISH),
                    2028,
                    TickingValue("2028", nextTick = 214.days),
                ),
                tuple(
                    DynamicYearLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = DynamicYearOptions(
                            absoluteOptions = YearOptions(eraStyle = EraStyle.ABBREVIATED),
                        )
                    ),
                    2020,
                    TickingValue("2020 AD", nextTick = null),
                ),
            ) { (localizer, year, expected) ->
                localizer.localizeAndTestNextTick(year, NOW) shouldBe expected
            }
        }
    }

    context("dates within relative threshold") {
        withTests(
            nameFn = { Pair(it.b, it.c).toString() },
            tuple(
                DynamicYearLocalizer(locale = LOCALE_ENGLISH),
                2026,
                TickingValue("this year", nextTick = 214.days),
            ),
            tuple(
                DynamicYearLocalizer(
                    locale = LOCALE_ENGLISH,
                    options = DynamicYearOptions(
                        relativeOptions = RelativeYearOptions(style = RelativeUnitStyle.SHORT)
                    ),
                ),
                2025,
                TickingValue("last yr.", nextTick = 214.days),
            ),
        ) { (localizer, year, expected) ->
            localizer.localizeAndTestNextTick(year, NOW) shouldBe expected
        }
    }

    context("custom threshold") {
        val localizer = DynamicYearLocalizer(
            locale = LOCALE_ITALIAN,
            options = DynamicYearOptions(relativeDiffRange = -10..5)
        )

        withTests(
            2015 to "2015",
            2016 to "10 anni fa",
            2026 to "quest’anno",
            2031 to "tra 5 anni",
            2032 to "2032",
        ) { (year, expected) ->
            localizer.localizeAndTestNextTick(year, NOW).value shouldBe expected
        }
    }

    context("nextTick") {
        val dateRange = LocalDateTime.parse("1950-01-01T00:00")..LocalDateTime.parse("2090-01-01T00:00:00")
        val localizer = DynamicYearLocalizer(locale = LOCALE_ENGLISH)

        localizer.nextTickPredictsChangeTest(
            arbitraryArb = Arb.year(min = 1950, max = 2090),
            smallArb = { Arb.element(it.toLocalDateTime().year) },
            referenceRange = dateRange.start.toInstant(TimeZone.UTC)..dateRange.endInclusive.toInstant(TimeZone.UTC),
        )
    }
}

class DynamicYearLocalizerTest : FunSpec({
    include(DynamicYearLocalizerTestFactory)
})
