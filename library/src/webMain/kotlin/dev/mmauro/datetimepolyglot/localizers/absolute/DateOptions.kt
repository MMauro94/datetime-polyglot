package dev.mmauro.datetimepolyglot.localizers.absolute

import js.intl.full
import js.intl.long
import js.intl.medium
import js.intl.short
import js.intl.DateStyle.Companion as JSDateStyle

internal fun DateStyle.toJsDateStyle() = when (this) {
    DateStyle.SHORT -> JSDateStyle.short
    DateStyle.MEDIUM -> JSDateStyle.medium
    DateStyle.LONG -> JSDateStyle.long
    DateStyle.FULL -> JSDateStyle.full
}