package dev.mmauro.datetimepolyglot.localizers.absolute

import io.kotest.core.spec.style.FunSpec
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

@RunWith(KotestTestRunner::class)
class YearLocalizerDeviceTest : FunSpec({
    include(YearLocalizerTestFactory)
})
