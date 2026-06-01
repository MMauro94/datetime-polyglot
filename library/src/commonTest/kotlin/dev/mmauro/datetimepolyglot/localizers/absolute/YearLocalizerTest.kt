package dev.mmauro.datetimepolyglot.localizers.absolute

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.TestPlatform
import dev.mmauro.datetimepolyglot.noPlatforms
import dev.mmauro.datetimepolyglot.noWeb
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.EraStyle
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.YearStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests

private const val YEAR = 123

val YearLocalizerTestFactory = funSpec {
    context("year style") {
        fun YearStyle.test() {
            val options = YearOptions(yearStyle = this)
            YearLocalizer(options, LOCALE_ENGLISH).localize(YEAR) shouldBeLocalizedAs when (this) {
                YearStyle.NUMERIC -> "123"
                YearStyle.NUMERIC_2_DIGITS -> "23"
                YearStyle.NUMERIC_PADDED_4_DIGITS -> "0123"
            }
        }

        withTests(YearStyle.entries - YearStyle.NUMERIC_PADDED_4_DIGITS) { yearStyle ->
            yearStyle.test()
        }

        test("NUMERIC_PADDED_4_DIGITS").config(
            enabledOrReasonIf = noWeb("This platform doesn't support padding request for year"),
        ) {
            YearStyle.NUMERIC_PADDED_4_DIGITS.test()
        }
    }
    context("era style") {
        withTests(EraStyle.entries) { eraStyle ->
            val options = YearOptions(
                eraStyle = eraStyle,
                yearStyle = YearStyle.NUMERIC,
            )
            YearLocalizer(options, LOCALE_ENGLISH).localize(YEAR) shouldBeLocalizedAs when (eraStyle) {
                EraStyle.NARROW -> "123 A"
                EraStyle.ABBREVIATED -> "123 AD"
                EraStyle.WIDE -> "123 Anno Domini"
            }
        }
    }
}

class YearLocalizerTest : FunSpec({ include(YearLocalizerTestFactory) })