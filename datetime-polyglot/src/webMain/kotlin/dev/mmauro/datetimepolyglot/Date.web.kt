package dev.mmauro.datetimepolyglot

import js.temporal.PlainDate
import js.temporal.PlainDateTime
import js.temporal.PlainTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.number
import kotlin.time.Instant
import js.temporal.Instant as JsInstant

internal fun LocalDateTime.toPlainDateTime(): PlainDateTime = date.toPlainDate().toPlainDateTime(time.toPlainTime())
internal fun LocalDate.toPlainDate(): PlainDate = PlainDate(year, month.number, day)
internal fun LocalTime.toPlainTime(): PlainTime {
    val milliseconds = nanosecond / 1000 / 1000
    val microseconds = (nanosecond / 1000) % 1000
    val nanoseconds = nanosecond % 1000
    return PlainTime(hour, minute, second, milliseconds, microseconds, nanoseconds)
}

internal fun Instant.toJsInstant(): JsInstant = JsInstant.from(toString())
