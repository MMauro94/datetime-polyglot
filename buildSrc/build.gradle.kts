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
}

gradlePlugin {
    plugins {
        create("dtp") {
            id = "dtp"
            implementationClass = "dev.mmauro.datetimepolyglot.buildlogic.DtpPlugin"
        }
    }
}