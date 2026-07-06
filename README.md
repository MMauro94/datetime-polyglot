# datetime-polyglot

[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central Version](https://img.shields.io/maven-central/v/dev.mmauro/datetime-polyglot?strategy=latestProperty)](https://central.sonatype.com/artifact/dev.mmauro/datetime-polyglot)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.0-blue?logo=kotlin)](http://kotlinlang.org)
[![API reference (KDoc)](https://img.shields.io/badge/API%20Reference-KDoc-blue)](https://datetime-polyglot.mmauro.dev/)

Multiplatform localization library for Kotlin date/time objects, either from stdlib
or [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime).

The library uses a different localization backend depending on the platform:

| Platform            | Localization backend                                                                          |
|---------------------|-----------------------------------------------------------------------------------------------|
| JVM                 | [ICU4J](https://unicode-org.github.io/icu/userguide/icu4j/)                                   |
| Android             | [android.icu](https://developer.android.com/guide/topics/resources/internationalization)      |
| JS (Browser + Node) | [Intl](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl) |
| WASM (Browser)      | [Intl](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Intl) |

> [!NOTE]
> While the library strives to provide a uniform API that mostly returns consistent values, subtle differences between
> various localization backends exist.
> You should not rely on localized strings being identical between platforms.

## Setup

Add to your dependencies:

```kotlin
dependencies {
    implementation("dev.mmauro:datetime-polyglot:<version>")
}
```

See latest version in badge above or look directly
at [Maven Central page](https://central.sonatype.com/artifact/dev.mmauro/datetime-polyglot).

<details>

<summary>Using the SNAPSHOT build</summary>

![Maven Central Snapshot Version](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fdev%2Fmmauro%2Fdatetime-polyglot%2Fmaven-metadata.xml&strategy=latestProperty&label=maven-central-snapshot)

Snapshot builds are published on every commit in `mainline`.

To use, add in your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        maven("https://central.sonatype.com/repository/maven-snapshots")
    }
}
```

Then add to your dependencies:

```kotlin
dependencies {
    implementation("dev.mmauro:datetime-polyglot:<version>")
}
```

See latest version in badge above or look
at [maven-metadata.xml](https://central.sonatype.com/repository/maven-snapshots/dev/mmauro/datetime-polyglot/maven-metadata.xml).

</details>

## Features

In general, each type of data that can be localized will have:

1. An options class that defines the settings for localization
2. A localizer class that accepts the options and a locale as constructor parameters
3. A utility `localize()` extension function on the data type that hides the construction of the localizer class

### Standalone date/time attributes

These should be used when the component to localize is standalone (e.g. calendar header), and should not be mixed with
other date components.

| Data type                                                                                                  | Localizer class / Extension function                                                                                                                                                | Examples                                                                    |
|------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| [`Month`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-month/)           | [`MonthLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.standalone/-month-localizer) <br> `Month.localize()`               | `January` <br> `Jan` <br> `J` <br> `1`                                      |
| [`DayOfWeek`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-day-of-week/) | [`DayOfWeekLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.standalone/-day-of-week-localizer) <br> `DayOfWeek.localize()` | `Monday` <br> `Mon` <br> `Mo` <br> `M`                                      |
| [`TimeZone`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-time-zone/)    | [`TimeZoneLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.standalone/-time-zone-localizer) <br> `TimeZone.localize()`     | `America/Los_Angeles` <br> `PT` <br> `Pacific Time` <br> `Los Angeles Time` |

### Absolute date/time

These should be used when you want to localize an absolute date/time object to show the user.
Avoid concatenating values from these localizers, always use the output of a localizer in full.

If you need only partial information, convert first to the appropriate type and then localize that.
For instance, if you have an `Instant` but are only interested in the time component, you should first convert to
`LocalDateTime`, then get the `LocalTime` part, and finally localize it.

| Data type                                                                                                                                                                              | Localizer class / Extension function                                                                                                                                                                                                                                                                                                                                           | Examples                                                                                                                                                                            |
|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`Zoned`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot/-zoned)<[`Instant`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-instant/)> | [`ZonedInstantLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-zoned-instant-localizer) <br> `ZonedInstantLocalizer.localize()`                                                                                                                                                                             | `1/8/26 9:05 PM PST` <br> `Jan 8, 2026, 9 at night Pacific Daylight Time` <br> `January 8, 2026 at 9:31:45 PM GMT-07:00` <br> `Thursday, January 8, 2026 at 21:05 Los Angeles Time` |
| [`LocalDateTime`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-date-time/)                                                                     | [`LocalDateTimeLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-local-date-time-localizer) <br> `LocalDateTime.localize()`                                                                                                                                                                                  | `1/8/26 9:05 PM` <br> `Jan 8, 2026, 9 at night` <br> `January 8, 2026 at 9:31:45 PM` <br> `Thursday, January 8, 2026 at 21:05`                                                      |
| [`LocalDate`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-date/)                                                                              | [`LocalDateLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-local-date-localizer) <br> `LocalDate.localize()`                                                                                                                                                                                               | `1/8/26` <br> `Jan 8, 2026` <br> `January 8, 2026` <br> `Thursday, January 8, 2026`                                                                                                 |
| [`LocalTime`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-time/)                                                                              | [`LocalTimeLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-local-time-localizer) <br> `LocalTime.localize()`                                                                                                                                                                                               | `9:05 PM` <br> `9:05:08 PM` <br> `21:05` <br> `21:05:08.123` <br> `9 at night`                                                                                                      |
| [`YearMonth`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-year-month/)                                                                              | [`YearMonthLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-year-month-localizer) <br> `YearMonth.localize()`                                                                                                                                                                                               | `January 2026` <br> `Jan 26` <br> `01/2026`                                                                                                                                         |
| Year ([`Int`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/))                                                                                                             | [`YearLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-year-localizer) <br> _No extension function_                                                                                                                                                                                                         | `2026` <br> `26` <br> `2026 AD` <br> `2026 Anno Domini`                                                                                                                             |
| [`Duration`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-duration/)                                                                                                     | [`DurationLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-duration-localizer) / [`TickingDurationLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-ticking-duration-localizer) <br> `Duration.localize()` / `Duration.localizeTicking()` | `1h 5m` <br> `1 hr, 5 min` <br> `1 hour, 5 minutes`                                                                                                                                 |

### Relative date/time

These should be used whenever you always want to localize a date/time object in a relative way.
Avoid concatenating values from these localizers, always use the output of a localizer in full.

All these functions return
a [`TickingValue`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot/-ticking-value),
class that holds the localized value and a "next tick" duration, which indicates for how long the value is valid for.
When the "next tick" expires, it means that the localized value now needs recomputation.

Most of these classes
implement [`PolyglotReferenceValueLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers/-polyglot-reference-value-localizer),
which allows to pass in a reference point (usually the current time) to use for relative localization.
As a convenience,
the [`localizeAsFlow`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers/localize-as-flow.html)
extension function will return
a [`Flow`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) that
automatically emits a new relative value when it is due.

| Data type                                                                                                 | Has reference point | Localizer class / Extension function                                                                                                                                                                                                                | Examples                                                                                           |
|-----------------------------------------------------------------------------------------------------------|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------|
| [`Duration`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-duration/)                        | No                  | [`RelativeDurationLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.relative/-relative-duration-localizer) <br> `Duration.relativeLocalize()`                                               | `10 minutes ago` <br> `in 1 hour` <br> `4h ago`                                                    |
| [`Instant`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-instant/)                          | Yes                 | [`RelativeInstantLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.relative/-relative-instant-localizer) <br> `Instant.localizeRelative()` <br> `Instant.localizeRelativeAsFlow()`          | `10 minutes ago` <br> `in 1 hour` <br> `4h ago`                                                    |
| [`LocalDate`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-date/) | Yes                 | [`RelativeLocalDateLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.relative/-relative-local-date-localizer) <br> `LocalDate.localizeRelative()` <br> `LocalDate.localizeRelativeAsFlow()` | `today` <br> `yesterday` <br> `this Monday` <br> `next Friday` <br> `in 54 days` <br> `5 days ago` |
| [`YearMonth`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-year-month/) | Yes                 | [`RelativeYearMonthLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.relative/-relative-year-month-localizer) <br> `YearMonth.localizeRelative()` <br> `YearMonth.localizeRelativeAsFlow()` | `this month` <br> `last mo.` <br> `next month` <br> `4 months ago` <br> `in 34mo`                  |
| Year ([`Int`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/))                                | Yes                 | [`RelativeYearLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.relative/-relative-year-localizer) <br> _No extension functions_                                                            | `last yr.` <br> `this year` <br> `next year` <br> `1 year ago` <br> `in 5y`                        |

