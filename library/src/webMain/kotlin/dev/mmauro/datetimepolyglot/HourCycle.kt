package dev.mmauro.datetimepolyglot

import js.intl.h11
import js.intl.h12
import js.intl.h23
import js.intl.h24
import js.intl.HourCycle as JsHourCycle

internal fun HourCycle.toJsHourCycle(): JsHourCycle = when (this) {
    HourCycle.HOURS_11 -> JsHourCycle.h11
    HourCycle.HOURS_12 -> JsHourCycle.h12
    HourCycle.HOURS_23 -> JsHourCycle.h23
    HourCycle.HOURS_24 -> JsHourCycle.h24
}