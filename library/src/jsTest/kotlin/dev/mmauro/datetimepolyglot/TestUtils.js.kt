package dev.mmauro.datetimepolyglot

actual val TEST_PLATFORM by lazy {
    val isBrowser = js("typeof window !== 'undefined' && typeof document !== 'undefined'") as Boolean
    if (isBrowser) TestPlatform.JS_BROWSER else TestPlatform.JS_NODE
}