package dev.mmauro.datetimepolyglot

enum class HourCycle(internal val is12Hour: Boolean) {
    HOURS_11(true),
    HOURS_12(true),
    HOURS_23(false),
    HOURS_24(false),
    ;

    companion object
}