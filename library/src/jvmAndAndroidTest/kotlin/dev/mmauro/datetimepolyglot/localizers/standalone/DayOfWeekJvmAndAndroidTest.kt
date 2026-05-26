package dev.mmauro.datetimepolyglot.localizers.standalone

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.styles.DayOfWeekStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DayOfWeek

val DayOfWeekJvmAndAndroidTestFactory = funSpec {
    test("SHORT style uses correct two-letter strings") {
        DayOfWeek.entries.map {
            it.localize(options = DayOfWeekOptions(style = DayOfWeekStyle.SHORT), LOCALE_ENGLISH)
        } shouldBe listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    }
}

class DayOfWeekJvmAndAndroidTest : FunSpec({
    include(DayOfWeekJvmAndAndroidTestFactory)
})