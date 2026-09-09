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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

const val DEFAULT_MIN_YEAR = 1950
const val DEFAULT_MAX_YEAR = 2090

val DEFAULT_MIN_DATE = LocalDateTime(DEFAULT_MIN_YEAR, Month.JANUARY, 1, 0, 0)
val DEFAULT_MAX_DATE = LocalDateTime(DEFAULT_MAX_YEAR, Month.DECEMBER, 31, 23, 59, 59, 999_999_999)

val DEFAULT_INSTANT_RANGE = DEFAULT_MIN_DATE.toInstant(TimeZone.UTC)..DEFAULT_MAX_DATE.toInstant(TimeZone.UTC)

fun Arb.Companion.year(min: Int = DEFAULT_MIN_YEAR, max: Int = DEFAULT_MAX_YEAR) = Arb.int(min = min, max = max)

fun Arb.Companion.yearMonth(
    min: YearMonth = YearMonth(DEFAULT_MIN_YEAR, Month.JANUARY),
    max: YearMonth = YearMonth(DEFAULT_MAX_YEAR, Month.DECEMBER),
) = Arb.bind(Arb.year(min = min.year, max = max.year), Arb.enum<Month>(), ::YearMonth).filter { it in min..max }

fun Arb.Companion.localDate(
    min: LocalDate = LocalDate(DEFAULT_MIN_YEAR, Month.JANUARY, 1),
    max: LocalDate = LocalDate(DEFAULT_MAX_YEAR, Month.DECEMBER, 31),
): Arb<LocalDate> {
    return Arb.long(min = min.toEpochDays(), max = max.toEpochDays()).map { LocalDate.fromEpochDays(it) }
}

fun Arb.Companion.localDateTime(
    min: LocalDateTime = DEFAULT_MIN_DATE,
    max: LocalDateTime = DEFAULT_MAX_DATE,
): Arb<LocalDateTime> {
    return Arb.kotlinInstant(
        minValue = min.toInstant(TimeZone.UTC),
        maxValue = max.toInstant(TimeZone.UTC),
    ).map {
        it.toLocalDateTime(TimeZone.UTC)
    }
}

fun Arb.Companion.timeZone() = Arb.element(TimeZone.availableZoneIds).map { TimeZone.of(it) }

fun <T> Arb.Companion.zoned(valueArb: Arb<T>): Arb<Zoned<T>> {
    return arbitrary {
        Zoned(
            value = valueArb.bind(),
            timeZone = Arb.timeZone().bind(),
        )
    }
}

fun Arb.Companion.zonedInstant(instantRange: KotlinInstantRange = Instant.DISTANT_PAST..Instant.DISTANT_FUTURE) =
    zoned(Arb.kotlinInstant(instantRange))
