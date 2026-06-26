package dev.mmauro.datetimepolyglot.utils

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class DurationTest : FunSpec({
    context("unitPart") {
        val duration = 69.days + 13.hours + 40.minutes + 21.seconds + 320.milliseconds + 255.microseconds + 420.nanoseconds
        withData(
            DurationUnit.DAYS to 69,
            DurationUnit.HOURS to 13,
            DurationUnit.MINUTES to 40,
            DurationUnit.SECONDS to 21,
            DurationUnit.MILLISECONDS to 320,
            DurationUnit.MICROSECONDS to 255,
            DurationUnit.NANOSECONDS to 420,
        ) { (unit, expected) ->
            withClue("positive duration") {
                duration.unitPart(unit) shouldBe expected.toLong()
            }

            withClue("negative duration") {
                (-duration).unitPart(unit) shouldBe -expected.toLong()
            }
        }

        context("zero duration") {
            withData(DurationUnit.entries) { unit ->
                Duration.ZERO.unitPart(unit) shouldBe 0
            }
        }
    }
})
