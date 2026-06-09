package dev.mmauro.datetimepolyglot

import io.kotest.core.test.Enabled
import io.kotest.core.test.EnabledOrReasonIf

sealed interface TestPlatform {
    data object Jvm : TestPlatform, PlatformMatcher {
        override fun matches(platform: TestPlatform) = platform is Jvm
    }

    data class Android(val sdk: Int) : TestPlatform {
        companion object : PlatformMatcher {
            override fun matches(platform: TestPlatform) = platform is Android

            fun atMost(sdk: Int) = PlatformMatcher { it is Android && it.sdk <= sdk }
        }
    }

    sealed interface Js : TestPlatform {

        data object Node : Js, PlatformMatcher {
            override fun matches(platform: TestPlatform) = platform is Node
        }

        data object Browser : Js, PlatformMatcher {
            override fun matches(platform: TestPlatform) = platform is Browser
        }

        companion object : PlatformMatcher {
            override fun matches(platform: TestPlatform) = platform is Js
        }
    }

    sealed interface Wasm : TestPlatform {

        data object Browser : Wasm, PlatformMatcher {
            override fun matches(platform: TestPlatform) = platform is Browser
        }

        companion object : PlatformMatcher {
            override fun matches(platform: TestPlatform) = platform is Wasm
        }
    }
}

expect val TEST_PLATFORM: TestPlatform

fun interface PlatformMatcher {
    fun matches(platform: TestPlatform): Boolean
}

operator fun PlatformMatcher.plus(another: PlatformMatcher) = PlatformMatcher { this.matches(it) || another.matches(it) }

fun noPlatforms(platforms: PlatformMatcher, reason: String): EnabledOrReasonIf = {
    Enabled(
        isEnabled = !platforms.matches(TEST_PLATFORM),
        reason = reason,
    )
}

fun noWeb(reason: String) = noPlatforms(TestPlatform.Js + TestPlatform.Wasm, reason)