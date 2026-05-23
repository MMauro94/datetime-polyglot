@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.mmauro.datetimepolyglot

@JsModule("@js-joda/timezone")
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule