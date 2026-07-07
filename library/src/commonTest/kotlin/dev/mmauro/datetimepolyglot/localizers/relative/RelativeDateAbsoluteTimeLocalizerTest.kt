package dev.mmauro.datetimepolyglot.localizers.relative

import dev.mmauro.datetimepolyglot.ALL_LOCALES
import dev.mmauro.datetimepolyglot.LOCALE_ENGLISH
import dev.mmauro.datetimepolyglot.LOCALE_ITALIAN
import dev.mmauro.datetimepolyglot.Zoned
import dev.mmauro.datetimepolyglot.localizers.absolute.DateStyle
import dev.mmauro.datetimepolyglot.localizers.absolute.TimeStyle
import dev.mmauro.datetimepolyglot.localizers.localDateTime
import dev.mmauro.datetimepolyglot.localizers.localizeAndTestNextTick
import dev.mmauro.datetimepolyglot.localizers.nextTickPredictsChangeTest
import dev.mmauro.datetimepolyglot.localizers.zonedInstant
import dev.mmauro.datetimepolyglot.shouldBeLocalizedAs
import dev.mmauro.datetimepolyglot.styles.RelativeUnitStyle
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.tuple
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * Current date used in most tests. This is a Thursday
 */
private val REFERENCE_DATE_TIME = LocalDateTime.parse("2026-01-01T00:00:00")
private val REFERENCE = Zoned(REFERENCE_DATE_TIME.toInstant(TimeZone.UTC), TimeZone.UTC)

val RelativeDateAbsoluteTimeLocalizerTestFactory = funSpec {
    context("localize") {
        context("localized value") {
            withTests(
                nameFn = { Pair(it.b, it.c).toString() },
                tuple(
                    RelativeDateAbsoluteTimeLocalizer(locale = LOCALE_ENGLISH),
                    LocalDateTime.parse("2026-01-16T03:00:00"),
                    "in 15 days at 3:00 AM",
                ),
                tuple(
                    RelativeDateAbsoluteTimeLocalizer(locale = LOCALE_ENGLISH),
                    LocalDateTime.parse("2025-12-31T21:00:00"),
                    "yesterday at 9:00 PM",
                ),
                tuple(
                    RelativeDateAbsoluteTimeLocalizer(locale = LOCALE_ENGLISH),
                    LocalDateTime.parse("2025-12-11T19:00:00"),
                    "21 days ago at 7:00 PM",
                ),
                tuple(
                    RelativeDateAbsoluteTimeLocalizer(locale = LOCALE_ENGLISH),
                    REFERENCE_DATE_TIME,
                    "today at 12:00 AM",
                ),
                tuple(
                    RelativeDateAbsoluteTimeLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = RelativeDateAbsoluteTimeOptions(
                            timeOptions = TimeStyle.Local.MEDIUM
                        ),
                    ),
                    LocalDateTime.parse("2026-01-01T04:13:05"),
                    "today at 4:13:05 AM",
                ),
                tuple(
                    RelativeDateAbsoluteTimeLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = RelativeDateAbsoluteTimeOptions(
                            dateOptions = RelativeLocalDateOptions(useRelativeDayOfWeek = true),
                        ),
                    ),
                    LocalDateTime.parse("2026-01-04T04:00:00"),
                    "next Sunday at 4:00 AM",
                ),
                tuple(
                    RelativeDateAbsoluteTimeLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = RelativeDateAbsoluteTimeOptions(
                            dateOptions = RelativeLocalDateOptions(
                                style = RelativeUnitStyle.SHORT,
                                useRelativeDayOfWeek = true,
                            ),
                        ),
                    ),
                    LocalDateTime.parse("2026-01-04T04:00:00"),
                    "next Sun. at 4:00 AM",
                ),
                tuple(
                    RelativeDateAbsoluteTimeLocalizer(
                        locale = LOCALE_ENGLISH,
                        options = RelativeDateAbsoluteTimeOptions(
                            dateOptions = RelativeLocalDateOptions(useRelativeDayOfWeek = true),
                            joinerStyle = DateStyle.SHORT
                        )
                    ),
                    LocalDateTime.parse("2026-01-04T04:00:00"),
                    "next Sunday, 4:00 AM",
                ),
                tuple(
                    RelativeDateAbsoluteTimeLocalizer(locale = LOCALE_ITALIAN),
                    LocalDateTime.parse("2025-12-31T21:00:00"),
                    "ieri alle ore 21:00",
                ),
                tuple(
                    RelativeDateAbsoluteTimeLocalizer(
                        locale = LOCALE_ITALIAN,
                        options = RelativeDateAbsoluteTimeOptions(
                            dateOptions = RelativeLocalDateOptions(useRelativeDayOfWeek = true),
                            joinerStyle = DateStyle.LONG,
                        )
                    ),
                    LocalDateTime.parse("2026-01-06T15:00:00"),
                    "martedì prossimo alle ore 15:00",
                ),
            ) { (localizer, localDateTime, expected) ->
                localizer.localizeAndTestNextTick(localDateTime, REFERENCE).value shouldBeLocalizedAs expected
            }


            test("must contain relative formatted local date") {
                checkAll(
                    Arb.element(ALL_LOCALES),
                    Arb.localDateTime(),
                    Arb.zonedInstant(),
                    Arb.enum<RelativeUnitStyle>(),
                ) { locale, localDateTime, now, relativeUnitStyle ->
                    val relativeDateOptions = RelativeLocalDateOptions(style = relativeUnitStyle)
                    val dateString = RelativeLocalDateLocalizer(
                        options = relativeDateOptions,
                        locale = locale
                    ).localize(localDateTime.date, now).value

                    val localized = RelativeDateAbsoluteTimeLocalizer(
                        locale = locale,
                        options = RelativeDateAbsoluteTimeOptions(dateOptions = relativeDateOptions)
                    ).localizeAndTestNextTick(localDateTime, now).value

                    localized shouldContain dateString
                }
            }
        }

        context("nextTick") {
            val localizer = RelativeDateAbsoluteTimeLocalizer(locale = LOCALE_ENGLISH)
            test("is always identical to RelativeLocalDateFormatter (as time is always absolute)") {
                val dateLocalizer = RelativeLocalDateLocalizer(locale = LOCALE_ENGLISH)
                checkAll(
                    Arb.localDateTime(),
                    Arb.zonedInstant(),
                ) { localDateTime, now ->
                    localizer.localize(localDateTime, now).nextTick shouldBe dateLocalizer.localize(localDateTime.date, now).nextTick
                }
            }

            localizer.nextTickPredictsChangeTest(
                arbitraryArb = Arb.localDateTime(),
                smallArb = { Arb.element(it.value.toLocalDateTime(it.timeZone)) },
            )
        }
    }

}

class RelativeDateAbsoluteTimeLocalizerTest : FunSpec({
    include(RelativeDateAbsoluteTimeLocalizerTestFactory)
})
