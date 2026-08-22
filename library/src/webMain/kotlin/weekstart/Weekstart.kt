@file:OptIn(ExperimentalWasmJsInterop::class)
@file:JsModule("weekstart")
@file:JsNonModule

package weekstart

import kotlinx.datetime.internal.JsNonModule
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsModule

internal external fun getWeekStartByLocale(locale: String): Int
internal external fun getWeekStartByRegion(region: String): Int
