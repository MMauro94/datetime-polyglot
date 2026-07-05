package dev.mmauro.datetimepolyglot.localizers

import dev.mmauro.datetimepolyglot.Zoned
import io.kotest.property.Arb
import io.kotest.property.arbitrary.KotlinInstantRange
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.kotlinInstant
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateRange
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlin.time.Instant

fun Arb.Companion.year(min: Int = 1800, max: Int = 2100) = Arb.int(min = min, max = max)

fun Arb.Companion.yearMonth(
    min: YearMonth = YearMonth(1800, Month.JANUARY),
    max: YearMonth = YearMonth(2100, Month.DECEMBER),
) = Arb.bind(Arb.year(min = min.year, max = max.year), Arb.enum<Month>(), ::YearMonth).filter { it in min..max }

fun Arb.Companion.localDate(
    min: LocalDate = LocalDate(1970, Month.JANUARY, 1),
    max: LocalDate = LocalDate(2030, Month.DECEMBER, 31),
): Arb<LocalDate> {
    return Arb.long(min = min.toEpochDays(), max = max.toEpochDays()).map { LocalDate.fromEpochDays(it) }
}

fun Arb.Companion.timeZone() = Arb.element(TimeZone.availableZoneIds).map { TimeZone.of(it) }

fun Arb.Companion.zonedInstant(instantRange: KotlinInstantRange = Instant.DISTANT_PAST..Instant.DISTANT_FUTURE) = arbitrary {
    Zoned(
        value = Arb.kotlinInstant(instantRange).bind(),
        timeZone = Arb.timeZone().bind(),
    )
}