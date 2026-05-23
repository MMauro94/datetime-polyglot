package dev.mmauro.datetimepolyglot

import io.kotest.matchers.shouldBe

private val NBSP = ' '

actual infix fun String.shouldBeLocalizedAs(value: String) {
    // On the JVM, ICU libraries sometimes put a non-breaking space
    // Let's normalize it so we can more freely test strings in a platform-agnostic way
    this.replace(NBSP, ' ') shouldBe value
}