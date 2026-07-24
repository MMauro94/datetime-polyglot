package dev.mmauro.datetimepolyglot.localizers.dynamic

import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.TickingValue
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.DEFAULT_INSTANT_RANGE
import dev.mmauro.datetimepolyglot.localizers.absolute.DateComponents
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalTimeComponents
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalTimeOptions
import dev.mmauro.datetimepolyglot.localizers.absolute.LocalTimeStyle
import dev.mmauro.datetimepolyglot.localizers.localDateTime
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.localizers.relative.RelativeLocalDateOptions
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.HourStyle
import dev.mmauro.datetimepolyglot.styles.MinuteStyle
import dev.mmauro.datetimepolyglot.styles.MonthStyle
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import dev.mmauro.datetimepolyglot.styles.SecondStyle
import dev.mmauro.datetimepolyglot.toLocalDateTime
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.tuple
import io.kotest.datatest.withTests
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.days

private val NOW_DATE = LocalDateTime.parse("2026-06-01T00:00:00")
private val NOW = Zoned(NOW_DATE.toInstant(TimeZone.UTC), TimeZone.UTC)

val DynamicDateAbsoluteTimeLocalizerTestFactory = funSpec {
    context("localize") {
        context("dates over relative threshold are completely absolute") {
            withTests(
                nameFn = { Pair(it.b, it.c).toString() },
                tuple(
                    DynamicDateAbsoluteTimeLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = DynamicDateAbsoluteTimeOptions(
                            absoluteDateOptions = DateStyle.SHORT,
                            timeOptions = LocalTimeOptions(LocalTimeStyle.SHORT),
                        )
                    ),
                    LocalDateTime.parse("2026-01-25T15:50:00"),
                    TickingValue("1/25/26, 3:50 PM", nextTick = null),
                ),
                tuple(
                    DynamicDateAbsoluteTimeLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = DynamicDateAbsoluteTimeOptions(
                            absoluteDateOptions = DateComponents(
                                monthStyle = MonthStyle.ABBREVIATED,
                            ),
                            timeOptions = LocalTimeOptions(
                                LocalTimeComponents(
                                    hourStyle = HourStyle.NUMERIC,
                                    minuteStyle = MinuteStyle.NUMERIC,
                                    secondStyle = SecondStyle.NUMERIC,
                                )
                            ),
                        )
                    ),
                    LocalDateTime.parse("2026-06-25T00:00:10"),
                    TickingValue("Jun 25, 2026, 12:00:10 AM", nextTick = 14.days),
                ),
            ) { (formatter, localDateTime, expected) ->
                formatter.localizeAndTestNextTick(localDateTime, NOW) shouldBeLocalizedAs expected
            }
        }
    }

    context("dates within relative threshold") {
        withTests(
            nameFn = { Pair(it.b, it.c).toString() },
            tuple(
                DynamicDateAbsoluteTimeLocalizer(
                    locale = LOCALE_ENGLISH,
                    options = DynamicDateAbsoluteTimeOptions(
                        absoluteDateOptions = DateStyle.SHORT,
                        timeOptions = LocalTimeOptions(LocalTimeStyle.SHORT),
                    )
                ),
                LocalDateTime.parse("2026-06-06T04:03:02"),
                TickingValue("in 5 days at 4:03 AM", nextTick = 1.days),
            ),
            tuple(
                DynamicDateAbsoluteTimeLocalizer(
                    locale = LOCALE_ENGLISH,
                    options = DynamicDateAbsoluteTimeOptions(
                        absoluteDateOptions = DateStyle.SHORT,
                        timeOptions = LocalTimeOptions(LocalTimeStyle.SHORT),
                        relativeDateOptions = RelativeLocalDateOptions(
                            style = RelativeUnitStyle.SHORT,
                        ),
                        relativeJoinerStyle = DateStyle.SHORT,
                    )
                ),
                LocalDateTime.parse("2026-05-26T15:00:00"),
                TickingValue("6 days ago, 3:00 PM", nextTick = 1.days),
            ),
        ) { (localizer, localDateTime, expected) ->
            localizer.localizeAndTestNextTick(localDateTime, NOW) shouldBeLocalizedAs expected
        }
    }

    context("custom threshold") {
        val localizer = DynamicDateAbsoluteTimeLocalizer(
            locale = LOCALE_ITALIAN,
            options = DynamicDateAbsoluteTimeOptions(
                absoluteDateOptions = DateStyle.MEDIUM,
                timeOptions = LocalTimeOptions(LocalTimeStyle.SHORT),
                relativeDateDiffRange = -2..14,
                relativeJoinerStyle = DateStyle.SHORT,
            )
        )

        withTests(
            nameFn = { it.first.toString() },
            LocalDateTime.parse("2026-05-28T10:00:00") to "28 mag 2026, 10:00",
            LocalDateTime.parse("2026-05-30T11:00:00") to "l’altro ieri, 11:00",
            LocalDateTime.parse("2026-06-01T12:00:00") to "oggi, 12:00",
            LocalDateTime.parse("2026-06-15T13:00:00") to "tra 14 giorni, 13:00",
            LocalDateTime.parse("2026-06-16T14:00:00") to "16 giu 2026, 14:00",
        ) { (localDateTime, expected) ->
            localizer.localizeAndTestNextTick(localDateTime, NOW).value shouldBeLocalizedAs expected
        }
    }

    context("nextTick") {
        val localizer = DynamicDateAbsoluteTimeLocalizer(
            locale = LOCALE_ENGLISH,
            options = DynamicDateAbsoluteTimeOptions(
                absoluteDateOptions = DateStyle.LONG,
                timeOptions = LocalTimeOptions(LocalTimeStyle.MEDIUM),
            )
        )

        localizer.nextTickPredictsChangeTest(
            arbitraryArb = Arb.localDateTime(),
            smallArb = { Arb.element(it.toLocalDateTime()) },
            referenceRange = DEFAULT_INSTANT_RANGE,
        )
    }
}

class DynamicDateAbsoluteTimeLocalizerTest : FunSpec({
    include(DynamicDateAbsoluteTimeLocalizerTestFactory)
})
