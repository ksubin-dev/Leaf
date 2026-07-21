import org.gradle.api.GradleException
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.Locale

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

data class CoverageArea(
    val name: String,
    val missed: Int,
    val covered: Int
) {
    val coverage: Double
        get() = coveragePercent(missed, covered)
}

fun coveragePercent(missed: Int, covered: Int): Double {
    val total = missed + covered
    return if (total == 0) 100.0 else covered.toDouble() / total * 100
}

fun formatCoverage(value: Double): String = String.format(Locale.US, "%.2f%%", value)

fun Map<String, String>.metric(key: String): Int = this[key]?.toIntOrNull() ?: 0

fun displayCoverageName(row: Map<String, String>): String {
    val packageName = row["PACKAGE"].orEmpty().replace("/", ".")
    val className = row["CLASS"].orEmpty()
    return if (packageName.isBlank()) className else "$packageName.$className"
}

fun isGeneratedCoverageName(name: String): Boolean {
    return name.contains(".dao.") && name.endsWith("_Impl") ||
        name.contains("Database_Impl") ||
        name.contains("RoomOpenDelegate") ||
        name.contains("_Factory") ||
        name.contains("_MembersInjector") ||
        name.contains("_ComponentTreeDeps") ||
        name.contains("ComposableSingletons")
}

fun readJacocoCsvRows(csvFile: File): List<Map<String, String>> {
    val lines = csvFile.readLines()
    if (lines.isEmpty()) return emptyList()

    val headers = lines.first().split(",")
    return lines.drop(1)
        .filter { it.isNotBlank() }
        .map { line ->
            val values = line.split(",")
            headers.mapIndexed { index, header ->
                header to values.getOrElse(index) { "" }
            }.toMap()
        }
}

subprojects {
    apply(plugin = "jacoco")

    tasks.withType<Test>().configureEach {
        extensions.configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
}

tasks.register("jacocoCoverageSummary") {
    group = "verification"
    description = "Generates a Markdown summary from the aggregate JaCoCo CSV report."

    dependsOn(tasks.named("jacocoTestReport"))

    val csvReport = layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.csv")
    val summaryReport = layout.buildDirectory.file("reports/jacoco/jacocoTestReport/coverage-summary.md")

    inputs.file(csvReport)
    outputs.file(summaryReport)

    doLast {
        val csvFile = csvReport.get().asFile
        if (!csvFile.exists()) {
            throw GradleException("JaCoCo CSV report not found: ${csvFile.absolutePath}")
        }

        val rows = readJacocoCsvRows(csvFile)
        val metrics = listOf(
            "Instruction" to "INSTRUCTION",
            "Branch" to "BRANCH",
            "Line" to "LINE",
            "Method" to "METHOD"
        )

        val lowCoverageRows = rows.mapNotNull { row ->
            val missed = row.metric("LINE_MISSED")
            val covered = row.metric("LINE_COVERED")
            val name = displayCoverageName(row)
            if (missed + covered == 0) {
                null
            } else if (isGeneratedCoverageName(name)) {
                null
            } else {
                CoverageArea(
                    name = name,
                    missed = missed,
                    covered = covered
                )
            }
        }.sortedWith(
            compareBy<CoverageArea> { it.coverage }
                .thenByDescending { it.missed }
                .thenBy { it.name }
        ).take(10)

        val zeroCoverageCount = rows.count { row ->
            val missed = row.metric("LINE_MISSED")
            val covered = row.metric("LINE_COVERED")
            val name = displayCoverageName(row)
            missed + covered > 0 && covered == 0 && !isGeneratedCoverageName(name)
        }

        val markdown = buildString {
            appendLine("## Coverage Summary")
            appendLine()
            appendLine("| Metric | Coverage | Covered | Missed |")
            appendLine("|---|---:|---:|---:|")

            metrics.forEach { (label, prefix) ->
                val missed = rows.sumOf { it.metric("${prefix}_MISSED") }
                val covered = rows.sumOf { it.metric("${prefix}_COVERED") }
                appendLine("| $label | ${formatCoverage(coveragePercent(missed, covered))} | $covered | $missed |")
            }

            appendLine()
            appendLine("## Low Coverage Areas")
            appendLine()
            appendLine("- Classes with 0% line coverage: $zeroCoverageCount")
            appendLine("- Generated Room/Hilt/Compose helper classes are excluded from this list.")
            appendLine()
            appendLine("| Class | Line Coverage | Covered Lines | Missed Lines |")
            appendLine("|---|---:|---:|---:|")

            if (lowCoverageRows.isEmpty()) {
                appendLine("| No line coverage data | - | - | - |")
            } else {
                lowCoverageRows.forEach { area ->
                    appendLine(
                        "| `${area.name}` | ${formatCoverage(area.coverage)} | ${area.covered} | ${area.missed} |"
                    )
                }
            }

            appendLine()
            appendLine("## Report Files")
            appendLine()
            appendLine("- HTML report artifact: `jacoco-html-report`")
            appendLine("- Markdown summary artifact: `jacoco-coverage-summary`")
            appendLine("- Source CSV: `build/reports/jacoco/jacocoTestReport/jacocoTestReport.csv`")
        }

        val summaryFile = summaryReport.get().asFile
        summaryFile.parentFile.mkdirs()
        summaryFile.writeText(markdown)
        logger.lifecycle("JaCoCo coverage summary generated: ${summaryFile.absolutePath}")
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
