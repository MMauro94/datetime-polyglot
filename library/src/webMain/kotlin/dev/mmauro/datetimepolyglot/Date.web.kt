package dev.mmauro.datetimepolyglot

import js.temporal.PlainDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlin.time.Instant
import js.temporal.Instant as JsInstant

internal fun LocalDate.toPlainDate(): PlainDate = PlainDate(year, month.number, day)
internal fun Instant.toJsInstant(): JsInstant = JsInstant.from(toString())
