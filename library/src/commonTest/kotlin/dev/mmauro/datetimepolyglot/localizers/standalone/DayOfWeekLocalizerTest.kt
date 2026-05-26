package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.LOCALE_POLISH
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DayOfWeek

val DayOfWeekLocalizerTestFactory = funSpec {
    test("basic test") {
        DayOfWeek.entries.map { it.localize(locale = LOCALE_ENGLISH) } shouldBe
                listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    }

    test("works in a different language") {
        DayOfWeek.entries.map { it.localize(locale = LOCALE_ITALIAN) } shouldBe
                listOf("lunedì", "martedì", "mercoledì", "giovedì", "venerdì", "sabato", "domenica")
    }

    context("works with different always-supported styles") {
        withTests(
            nameFn = { it.first.name },
            DayOfWeekStyle.NARROW to listOf("M", "T", "W", "T", "F", "S", "S"),
            DayOfWeekStyle.ABBREVIATED to listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
        ) { (style, expected) ->
            DayOfWeek.entries.map { it.localize(DayOfWeekOptions(style = style)) } shouldBe expected
        }
    }

    context("uses standalone case for languages that make the distinction") {
        DayOfWeek.entries.map { it.localize(locale = LOCALE_POLISH) } shouldBe
                listOf("poniedziałek", "wtorek", "środa", "czwartek", "piątek", "sobota", "niedziela")
    }
}

class DayOfWeekLocalizerTest : FunSpec({
    include(DayOfWeekLocalizerTestFactory)
})