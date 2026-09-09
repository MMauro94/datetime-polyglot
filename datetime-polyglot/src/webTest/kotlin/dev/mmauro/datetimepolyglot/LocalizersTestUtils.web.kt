package dev.mmauro.datetimepolyglot

import io.kotest.matchers.shouldBe

actual infix fun String.shouldBeLocalizedAs(value: String) {
    this shouldBe value
}
