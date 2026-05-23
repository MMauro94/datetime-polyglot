package dev.mmauro.datetimepolyglot.localizers.component

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DayOfWeek

val DayOfWeekWebTestFactory = funSpec {
    test("SHORT style uses falls back to three letter strings") {
        DayOfWeek.entries.map {
            it.localize(LOCALE_ENGLISH, options = DayOfWeekOptions(style = DayOfWeekStyle.SHORT))
        } shouldBe listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    }
}

class DayOfWeekWebTest : FunSpec({
    include(DayOfWeekWebTestFactory)
})