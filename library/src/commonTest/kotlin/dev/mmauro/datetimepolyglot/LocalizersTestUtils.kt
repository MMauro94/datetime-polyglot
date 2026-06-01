package dev.mmauro.datetimepolyglot

import io.kotest.inspectors.forAny

expect infix fun String.shouldBeLocalizedAs(value: String)

infix fun String.shouldBeLocalizedAsOneOf(values: List<String>) {
    values.forAny { this shouldBeLocalizedAs it }
}