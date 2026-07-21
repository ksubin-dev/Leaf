import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.ksp) apply false
    jacoco
}

jacoco {
    toolVersion = "0.8.13"
}

val coverageExclusions = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "**/*Hilt*.*",
    "**/*Dagger*.*",
    "**/*_Factory*.*",
    "**/*_MembersInjector*.*",
    "**/*_ComponentTreeDeps*.*",
    "**/*ComposableSingletons*.*",
    "**/*Preview*.*"
)

subprojects {
    apply(plugin = "jacoco")

    tasks.withType<Test>().configureEach {
        extensions.configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Generates an aggregate JaCoCo report for JVM and Android debug unit tests."

    val unitTestTasks = subprojects.map { subproject ->
        subproject.tasks.matching { task ->
            task.name == "testDebugUnitTest" ||
                (task.name == "test" && subproject.plugins.hasPlugin("org.jetbrains.kotlin.jvm"))
        }
    }

    dependsOn(unitTestTasks)

    executionData.setFrom(
        fileTree(rootDir) {
            include(
                "**/build/jacoco/*.exec",
                "**/build/outputs/unit_test_code_coverage/**/*.exec"
            )
        }
    )

    sourceDirectories.setFrom(
        files(
            subprojects.flatMap { subproject ->
                listOf(
                    subproject.file("src/main/java"),
                    subproject.file("src/main/kotlin")
                )
            }
        )
    )

    classDirectories.setFrom(
        files(
            subprojects.flatMap { subproject ->
                val buildDir = subproject.layout.buildDirectory.get().asFile
                listOf(
                    fileTree("$buildDir/tmp/kotlin-classes/debug") {
                        exclude(coverageExclusions)
                    },
                    fileTree("$buildDir/intermediates/javac/debug/classes") {
                        exclude(coverageExclusions)
                    },
                    fileTree("$buildDir/classes/kotlin/main") {
                        exclude(coverageExclusions)
                    },
                    fileTree("$buildDir/classes/java/main") {
                        exclude(coverageExclusions)
                    }
                )
            }
        )
    )

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/jacocoTestReport/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.xml"))
        csv.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.csv"))
    }
}
