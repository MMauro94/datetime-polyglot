package dev.mmauro.datetimepolyglot.localizers.component

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DayOfWeek

val DayOfWeekJvmAndAndroidTestFactory = funSpec {
    test("SHORT style uses correct two-letter strings") {
        DayOfWeek.entries.map {
            it.localize(LOCALE_ENGLISH, options = DayOfWeekOptions(style = DayOfWeekStyle.SHORT))
        } shouldBe listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    }
}

class DayOfWeekJvmAndAndroidTest : FunSpec({
    include(DayOfWeekJvmAndAndroidTestFactory)
})