package dev.mmauro.datetimepolyglot

import js.date.Date
import kotlinx.datetime.LocalDate

internal fun LocalDate.toJsDate(): Date = Date(toString())
