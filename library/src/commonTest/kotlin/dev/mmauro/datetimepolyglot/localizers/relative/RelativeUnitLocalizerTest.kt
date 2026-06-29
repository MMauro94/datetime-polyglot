package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.LOCALE_POLISH
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

class RelativeUnitLocalizerTest : FunSpec({
    context("localizeNumeric") {
        test("past") {
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
            localizer.localizeNumeric(-1.0, RelativeUnit.DAY) shouldBeLocalizedAs "1 day ago"
        }
        test("now") {
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
            localizer.localizeNumeric(0.0, RelativeUnit.DAY) shouldBeLocalizedAs "in 0 days"
        }
        test("future") {
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
            localizer.localizeNumeric(1.0, RelativeUnit.DAY) shouldBeLocalizedAs "in 1 day"
        }

        context("styles") {
            withTests(RelativeUnitStyle.entries) { style ->
                val localizer = RelativeUnitLocalizer(style = style, locale = LOCALE_ENGLISH)
                localizer.localizeNumeric(-1.0, RelativeUnit.MINUTE) shouldBeLocalizedAs when (style) {
                    RelativeUnitStyle.NARROW -> "1m ago"
                    RelativeUnitStyle.SHORT -> "1 min. ago"
                    RelativeUnitStyle.LONG -> "1 minute ago"
                }
            }
        }

        context("different locale") {
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.SHORT, locale = LOCALE_ITALIAN)
            localizer.localizeNumeric(5.0, RelativeUnit.WEEK) shouldBeLocalizedAs "tra 5 sett."
        }
    }

    context("localizeDirection") {
        context("day") {
            withTests(RelativeDirection.entries) { direction ->
                val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
                localizer.localizeDirection(direction, RelativeUnit.DAY) shouldBe when (direction) {
                    RelativeDirection.LAST_2 -> null
                    RelativeDirection.LAST -> "yesterday"
                    RelativeDirection.THIS -> "today"
                    RelativeDirection.NEXT -> "tomorrow"
                    RelativeDirection.NEXT_2 -> null
                }
            }
        }
        context("year") {
            withTests(RelativeDirection.entries) { direction ->
                val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ENGLISH)
                localizer.localizeDirection(direction, RelativeUnit.YEAR) shouldBe when (direction) {
                    RelativeDirection.LAST_2 -> null
                    RelativeDirection.LAST -> "last year"
                    RelativeDirection.THIS -> "this year"
                    RelativeDirection.NEXT -> "next year"
                    RelativeDirection.NEXT_2 -> null
                }
            }
        }

        context("styles") {
            withTests(RelativeUnitStyle.entries) { style ->
                val localizer = RelativeUnitLocalizer(style = style, locale = LOCALE_ENGLISH)
                localizer.localizeDirection(RelativeDirection.LAST, RelativeUnit.MONTH).shouldNotBeNull() shouldBeLocalizedAs when (style) {
                    RelativeUnitStyle.NARROW -> "last mo."
                    RelativeUnitStyle.SHORT -> "last mo."
                    RelativeUnitStyle.LONG -> "last month"
                }
            }
        }

        context("different locale") {
            val localizer = RelativeUnitLocalizer(style = RelativeUnitStyle.LONG, locale = LOCALE_ITALIAN)
            localizer.localizeDirection(RelativeDirection.LAST_2, RelativeUnit.DAY).shouldNotBeNull() shouldBeLocalizedAs "l’altro ieri"
        }

        test("should never be formatted identically as localizeNumeric") {
            checkAll(
                Arb.enum<RelativeDirection>(),
                Arb.enum<RelativeUnit>(),
                Arb.enum<RelativeUnitStyle>(),
                listOf(LOCALE_ENGLISH, LOCALE_ITALIAN, LOCALE_POLISH).exhaustive(),
            ) { direction, relativeUnit, style, locale ->
                val localizer = RelativeUnitLocalizer(style, locale)

                localizer.localizeDirection(direction, relativeUnit)
                    .shouldNotBe(localizer.localizeNumeric(direction.offset.toDouble(), relativeUnit))
            }
        }
    }
})