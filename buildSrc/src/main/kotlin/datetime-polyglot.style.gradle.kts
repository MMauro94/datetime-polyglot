import dev.detekt.gradle.Detekt
import kotlin.io.path.div
import kotlin.io.path.Path

plugins {
    id("org.jlleitschuh.gradle.ktlint")
    id("dev.detekt")
}

ktlint {
    version = "1.8.0"
}

detekt {
    config.setFrom(rootDir.toPath() / "detekt.yml")
    buildUponDefaultConfig = true
}


val detektProjectDir = projectDir
tasks.withType<Detekt> {
    exclude {
        // Exclude auto generated files
        it.file.toPath().contains(Path("build"))
    }
}

tasks.register("detektAll") {
    group = "verification"
    description = "Runs Detekt across KMP source sets and compilations."

    dependsOn(
        tasks.matching {
            it is Detekt &&
                it.name != "detekt" && // avoid main "detekt" task, as it runs without type resolution
                //!it.name.endsWith("SourceSet") && // SourceSet tasks run without type resolution
                it.name != name // must not be this task, avoid dependency on itself
        },
    )
}

tasks.named("check") {
    dependsOn("detektAll")
}
