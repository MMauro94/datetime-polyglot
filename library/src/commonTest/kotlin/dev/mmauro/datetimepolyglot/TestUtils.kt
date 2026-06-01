package dev.mmauro.datetimepolyglot

import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledOrReasonIf

enum class TestPlatform {
    JVM,
    ANDROID,
    JS_NODE,
    JS_BROWSER,
    WASM_JS_BROWSER,
}

expect val TEST_PLATFORM: TestPlatform

fun noPlatforms(platforms: Set<TestPlatform>, reason: String): EnabledOrReasonIf = {
    Enabled(
        isEnabled = TEST_PLATFORM !in platforms,
        reason = reason,
    )
}

fun noNodeJs(reason: String) = noPlatforms(setOf(TestPlatform.JS_NODE), reason)
fun noJs(reason: String) = noPlatforms(setOf(TestPlatform.JS_NODE, TestPlatform.JS_BROWSER, TestPlatform.WASM_JS_BROWSER), reason)
fun noWeb(reason: String) = noPlatforms(setOf(TestPlatform.JS_NODE, TestPlatform.JS_BROWSER, TestPlatform.WASM_JS_BROWSER), reason)