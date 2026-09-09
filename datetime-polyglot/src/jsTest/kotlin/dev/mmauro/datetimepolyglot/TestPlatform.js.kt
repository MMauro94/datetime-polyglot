package dev.mmauro.datetimepolyglot

actual val TEST_PLATFORM: TestPlatform by lazy {
    val isBrowser = js("typeof window !== 'undefined' && typeof document !== 'undefined'") as Boolean
    if (isBrowser) TestPlatform.Js.Browser else TestPlatform.Js.Node
}
