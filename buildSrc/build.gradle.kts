plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.z4kn4fein:semver:3.1.0")
}

gradlePlugin {
    plugins {
        create("dtp") {
            id = "dtp"
            implementationClass = "dev.mmauro.datetimepolyglot.buildlogic.DtpPlugin"
        }
    }
}