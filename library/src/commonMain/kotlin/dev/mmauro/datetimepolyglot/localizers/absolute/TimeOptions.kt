package dev.mmauro.datetimepolyglot.localizers.absolute

sealed interface TimeOptions

enum class TimeStyle : TimeOptions {
    SHORT,
    MEDIUM,
    LONG,
    FULL,
}
