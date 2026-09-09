package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.LOCALE_POLISH
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Month

val MonthLocalizerTestFactory = funSpec {
    context("basic test") {
        withTests(Month.entries) { month ->
            month.localize(locale = LOCALE_ENGLISH) shouldBe when (month) {
                Month.JANUARY -> "January"
                Month.FEBRUARY -> "February"
                Month.MARCH -> "March"
                Month.APRIL -> "April"
                Month.MAY -> "May"
                Month.JUNE -> "June"
                Month.JULY -> "July"
                Month.AUGUST -> "August"
                Month.SEPTEMBER -> "September"
                Month.OCTOBER -> "October"
                Month.NOVEMBER -> "November"
                Month.DECEMBER -> "December"
            }
        }
    }

    context("works in a different language") {
        withTests(Month.entries) { month ->
            month.localize(locale = LOCALE_ITALIAN) shouldBe when (month) {
                Month.JANUARY -> "gennaio"
                Month.FEBRUARY -> "febbraio"
                Month.MARCH -> "marzo"
                Month.APRIL -> "aprile"
                Month.MAY -> "maggio"
                Month.JUNE -> "giugno"
                Month.JULY -> "luglio"
                Month.AUGUST -> "agosto"
                Month.SEPTEMBER -> "settembre"
                Month.OCTOBER -> "ottobre"
                Month.NOVEMBER -> "novembre"
                Month.DECEMBER -> "dicembre"
            }
        }
    }

    context("works with different styles") {
        withTests(
            nameFn = { it.first.name },
            MonthStyle.NUMERIC to listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"),
            MonthStyle.NUMERIC_PADDED_2_DIGITS to listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"),
            MonthStyle.NARROW to listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"),
            MonthStyle.ABBREVIATED to listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
        ) { (style, expected) ->
            Month.entries.map { it.localize(MonthOptions(style = style), LOCALE_ENGLISH) } shouldBe expected
        }
    }

    context("uses standalone case for languages that make the distinction") {
        withTests(Month.entries) { month ->
            month.localize(locale = LOCALE_POLISH) shouldBe when (month) {
                Month.JANUARY -> "styczeń"
                Month.FEBRUARY -> "luty"
                Month.MARCH -> "marzec"
                Month.APRIL -> "kwiecień"
                Month.MAY -> "maj"
                Month.JUNE -> "czerwiec"
                Month.JULY -> "lipiec"
                Month.AUGUST -> "sierpień"
                Month.SEPTEMBER -> "wrzesień"
                Month.OCTOBER -> "październik"
                Month.NOVEMBER -> "listopad"
                Month.DECEMBER -> "grudzień"
            }
        }
    }
}

class MonthLocalizerTest : FunSpec({
    include(MonthLocalizerTestFactory)
})
