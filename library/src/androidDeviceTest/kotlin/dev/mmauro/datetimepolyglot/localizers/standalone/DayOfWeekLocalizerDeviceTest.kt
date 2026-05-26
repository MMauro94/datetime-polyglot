package dev.mmauro.datetimepolyglot.localizers.standalone

import io.kotest.core.spec.style.FunSpec
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

@RunWith(KotestTestRunner::class)
class DayOfWeekLocalizerDeviceTest : FunSpec({
    include(DayOfWeekLocalizerTestFactory)
    include(DayOfWeekJvmAndAndroidTestFactory)
})