package dev.mmauro.datetimepolyglot.localizers.standalone

import io.kotest.core.spec.style.FunSpec
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

@RunWith(KotestTestRunner::class)
class MonthLocalizerDeviceTest : FunSpec({
    include(MonthLocalizerTestFactory)
})
