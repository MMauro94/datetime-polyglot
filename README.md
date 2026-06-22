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

This library is still **WIP**, no release has been published yet.

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

These should be used when the component to format is standalone (e.g. calendar header), and should not be mixed with
other date components.

| Data type                                                                                                  | Localizer class / Extension function                                                                                                                                                           | Examples                                                                    |
|------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| [`Month`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-month/)           | [`MonthLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.standalone/-month-localizer/index.html) <br> `Month.localize()`               | `January` <br> `Jan` <br> `J` <br> `1`                                      |
| [`DayOfWeek`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-day-of-week/) | [`DayOfWeekLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.standalone/-day-of-week-localizer/index.html) <br> `DayOfWeek.localize()` | `Monday` <br> `Mon` <br> `Mo` <br> `M`                                      |
| [`TimeZone`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-time-zone/)    | [`TimeZoneLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.standalone/-time-zone-localizer/index.html) <br> `TimeZone.localize()`     | `America/Los_Angeles` <br> `PT` <br> `Pacific Time` <br> `Los Angeles Time` |

### Absolute date/time

This should be used when you want to format an absolute date/time object to show the user.
Avoid concatenating values from these localizers, always use the output of a localizer in full.

If you need only partial information, convert first to the appropriate type and then localize that.
For instance, if you have an `Instant` but are only interested in the time component, you should first convert to
`LocalDateTime`, then get the `LocalTime` part, and finally localize it.

| Data type                                                                                                                                                                                         | Localizer class / Extension function                                                                                                                                                                          | Examples                                                                                                                                                                            |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`Zoned`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot/-zoned/index.html)<[`Instant`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.time/-instant/)> | [`ZonedInstantLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-zoned-instant-localizer/index.html) <br> `ZonedInstantLocalizer.localize()` | `1/8/26 9:05 PM PST` <br> `Jan 8, 2026, 9 at night Pacific Daylight Time` <br> `January 8, 2026 at 9:31:45 PM GMT-07:00` <br> `Thursday, January 8, 2026 at 21:05 Los Angeles Time` |
| [`LocalDateTime`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-date-time/)                                                                                | [`LocalDateTimeLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-local-date-time-localizer/index.html) <br> `LocalDateTime.localize()`      | `1/8/26 9:05 PM` <br> `Jan 8, 2026, 9 at night` <br> `January 8, 2026 at 9:31:45 PM` <br> `Thursday, January 8, 2026 at 21:05`                                                      |
| [`LocalDate`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-date/)                                                                                         | [`LocalDateLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-local-date-localizer/index.html) <br> `LocalDate.localize()`                   | `1/8/26` <br> `Jan 8, 2026` <br> `January 8, 2026` <br> `Thursday, January 8, 2026`                                                                                                 |
| [`LocalTime`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-local-time/)                                                                                         | [`LocalTimeLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-local-time-localizer/index.html) <br> `LocalTime.localize()`                   | `9:05 PM` <br> `9:05:08 PM` <br> `21:05` <br> `21:05:08.123` <br> `9 at night`                                                                                                      |
| [`YearMonth`](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/-year-month/)                                                                                         | [`YearMonthLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-year-month-localizer/index.html) <br> `YearMonth.localize()`                   | `January 2026` <br> `Jan 26` <br> `01/2026`                                                                                                                                         |
| Year ([`Int`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/))                                                                                                                        | [`YearLocalizer`](https://datetime-polyglot.mmauro.dev/datetime-polyglot/dev.mmauro.datetimepolyglot.localizers.absolute/-year-localizer/index.html) <br> _No extension function_                             | `2026` <br> `26` <br> `2026 AD` <br> `2026 Anno Domini`                                                                                                                             |

