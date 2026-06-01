package dev.mmauro.datetimepolyglot.localizers.absolute

import js.intl.full
import js.intl.long
import js.intl.medium
import js.intl.short
import js.intl.TimeStyle.Companion as JsTimeStyle

internal fun TimeStyle.toJsTimeStyle() = when (this) {
    TimeStyle.Local.SHORT -> JsTimeStyle.short
    TimeStyle.Local.MEDIUM -> JsTimeStyle.medium
    TimeStyle.Zoned.LONG -> JsTimeStyle.long
    TimeStyle.Zoned.FULL -> JsTimeStyle.full
}