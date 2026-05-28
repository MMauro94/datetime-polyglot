package dev.mmauro.datetimepolyglot.localizers.absolute

import js.intl.full
import js.intl.long
import js.intl.medium
import js.intl.short
import js.intl.TimeStyle.Companion as JsTimeStyle

internal fun TimeStyle.toJsTimeStyle() = when (this) {
    TimeStyle.SHORT -> JsTimeStyle.short
    TimeStyle.MEDIUM -> JsTimeStyle.medium
    TimeStyle.LONG -> JsTimeStyle.long
    TimeStyle.FULL -> JsTimeStyle.full
}