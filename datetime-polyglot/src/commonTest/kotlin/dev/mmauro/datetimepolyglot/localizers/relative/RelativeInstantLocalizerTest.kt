package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import io.kotest.core.spec.style.FunSpec
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class RelativeInstantLocalizerTest : FunSpec({
    // There's not much to test here, as all the logic is forwarded to RelativeDurationLocalizer
    context("smoke tests") {
        val instantA = Instant.parse("2026-04-05T10:00:00.000Z")
        val instantB = Instant.parse("2026-04-05T11:15:00.000Z")
        test("past") {
            instantA.localizeRelative(reference = instantB, locale = LOCALE_ENGLISH) shouldBeLocalizedAs TickingValue(
                value = "1 hour ago",
                nextTick = 45.minutes,
            )
        }
        test("now") {
            instantA.localizeRelative(reference = instantA, locale = LOCALE_ENGLISH) shouldBeLocalizedAs TickingValue(
                value = "now",
                nextTick = 1.seconds,
            )
        }
        test("future") {
            instantB.localizeRelative(reference = instantA, locale = LOCALE_ENGLISH) shouldBeLocalizedAs TickingValue(
                value = "in 1 hour",
                nextTick = 15.minutes + 1.nanoseconds,
            )
        }
    }
})
