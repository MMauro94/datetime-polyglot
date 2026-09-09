package dev.mmauro.datetimepolyglot

import io.kotest.matchers.shouldBe

expect infix fun String.shouldBeLocalizedAs(value: String)

infix fun TickingValue<String>.shouldBeLocalizedAs(other: TickingValue<String>) {
    this.value shouldBeLocalizedAs other.value
    this.nextTick shouldBe other.nextTick
}
