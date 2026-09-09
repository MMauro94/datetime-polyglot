package dev.mmauro.datetimepolyglot.localizers.dynamic

import io.kotest.core.spec.style.FunSpec
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

@RunWith(KotestTestRunner::class)
class DynamicDateAbsoluteTimeLocalizerDeviceTest : FunSpec({
    include(DynamicDateAbsoluteTimeLocalizerTestFactory)
})
