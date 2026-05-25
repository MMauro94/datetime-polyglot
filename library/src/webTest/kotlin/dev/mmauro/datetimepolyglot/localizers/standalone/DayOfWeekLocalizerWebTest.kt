package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DayOfWeek

val DayOfWeekLocalizerWebTestFactory = funSpec {
    test("SHORT style uses falls back to three letter strings") {
        DayOfWeek.entries.map {
            it.localize(LOCALE_ENGLISH, options = DayOfWeekOptions(style = DayOfWeekStyle.SHORT))
        } shouldBe listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    }
}

class DayOfWeekLocalizerWebTest : FunSpec({
    include(DayOfWeekLocalizerWebTestFactory)
})