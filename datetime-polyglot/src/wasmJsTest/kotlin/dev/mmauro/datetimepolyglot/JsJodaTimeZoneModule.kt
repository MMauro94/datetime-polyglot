@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.mmauro.datetimepolyglot

@JsModule("@js-joda/timezone")
external object JsJodaTimeZoneModule

// Needed to initialize timezones
private val jsJodaTz = JsJodaTimeZoneModule
