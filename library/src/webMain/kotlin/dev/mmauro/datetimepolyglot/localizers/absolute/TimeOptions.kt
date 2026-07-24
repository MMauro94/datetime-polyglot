package dev.mmauro.datetimepolyglot.localizers.absolute

import js.intl.full
import js.intl.long
import js.intl.medium
import js.intl.short
import js.intl.TimeStyle.Companion as JsTimeStyle

internal fun TimeStyle.toJsTimeStyle() = when (this) {
    LocalTimeStyle.SHORT -> JsTimeStyle.short
    LocalTimeStyle.MEDIUM -> JsTimeStyle.medium
    ZonedTimeStyle.LONG -> JsTimeStyle.long
    ZonedTimeStyle.FULL -> JsTimeStyle.full
}