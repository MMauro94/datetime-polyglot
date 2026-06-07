# datetime-polyglot

![Maven Central Version](https://img.shields.io/maven-central/v/dev.mmauro/datetime-polyglot?strategy=latestProperty)

Multiplatform localization library for Kotlin date/time.


## Setup

This library is still **WIP**, no release published yet.

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
kotlin {
    dependencies {
        implementation("dev.mmauro:datetime-polyglot:<version>")
    }    
}
```

See latest version in badge above or look at [maven-metadata.xml](https://central.sonatype.com/repository/maven-snapshots/dev/mmauro/datetime-polyglot/maven-metadata.xml).

</details>