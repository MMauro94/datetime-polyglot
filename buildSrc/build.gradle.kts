plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.semver)
    implementation(libs.kotlinpoet)
    implementation(libs.ibm.icu4j)
    implementation(libs.kotlinx.datetime)
}

gradlePlugin {
    plugins {
        create("dtp") {
            id = "dtp"
            implementationClass = "dev.mmauro.datetimepolyglot.buildlogic.DtpPlugin"
        }
    }
}