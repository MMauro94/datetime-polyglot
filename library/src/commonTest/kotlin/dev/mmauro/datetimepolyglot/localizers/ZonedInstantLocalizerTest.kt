package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.HourCycle
import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

val KOTLIN_CONF_2026_START_DATE = Zoned(
    value = Instant.parse("2026-05-21T07:00:00.000Z"),
    timeZone = TimeZone.of("Europe/Berlin")
)

val ZonedInstantLocalizerTestFactory = funSpec {
    test("basic test") {
        KOTLIN_CONF_2026_START_DATE.localize(
            locale = LOCALE_ENGLISH,
            options = ZonedInstantOptions(
                yearStyle = YearStyle.NUMERIC,
                monthStyle = MonthStyle.WIDE,
                dayOfMonthStyle = DayOfMonthStyle.NUMERIC,
                dayOfWeekStyle = DayOfWeekStyle.WIDE,
                dayPeriodStyle = DayPeriodStyle.FLEXIBLE_WIDE,
                hourStyle = HourStyle.NUMERIC,
                hourCycle = HourCycle.HOURS_12,
                minuteStyle = MinuteStyle.NUMERIC_PADDED_2_DIGITS,
                timezoneStyle = TimeZoneStyle.GENERIC_NON_LOCATION_LONG,
            )
        ) shouldBeLocalizedAs "Thursday, May 21, 2026 at 9:00 in the morning Central European Time"
    }
    // TODO add more tests
    // TODO verify what can be done for numeric padded misbehavior in JVM
}

class ZonedInstantLocalizerTest : FunSpec({
    include(ZonedInstantLocalizerTestFactory)
})