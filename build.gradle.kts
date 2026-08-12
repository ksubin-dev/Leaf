import org.gradle.api.GradleException
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.time.Instant
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
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.androidx.benchmark) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
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
    val layer: String,
    val missed: Int,
    val covered: Int
) {
    val coverage: Double
        get() = coveragePercent(missed, covered)
}

data class CoverageLayerSummary(
    val layer: String,
    val missed: Int,
    val covered: Int,
    val zeroCoverageCount: Int
) {
    val coverage: Double
        get() = coveragePercent(missed, covered)
}

val coreQualityLayers = listOf(
    "Worker",
    "Repository",
    "DataSource",
    "Mapper",
    "ViewModel",
    "UseCase",
    "Database",
    "Sync/Upload Queue",
    "Utility"
)

val uiCoverageLayers = listOf(
    "Compose UI",
    "Navigation",
    "StateHolder"
)

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

fun classifyCoverageLayer(name: String): String {
    val lower = name.lowercase(Locale.US)
    return when {
        lower.contains("uploadqueue") -> "Sync/Upload Queue"
        lower.contains("worker") -> "Worker"
        lower.contains("repository") -> "Repository"
        lower.contains("datasource") -> "DataSource"
        lower.contains("mapper") -> "Mapper"
        lower.contains("viewmodel") -> "ViewModel"
        lower.contains("usecase") -> "UseCase"
        lower.contains(".dao.") || lower.contains("database") || lower.contains(".room.") -> "Database"
        lower.contains("sync") -> "Sync/Upload Queue"
        lower.contains("uistate") || lower.contains("stateholder") -> "StateHolder"
        lower.contains("navigation") || lower.contains("navgraph") || lower.contains("route") -> "Navigation"
        lower.contains("screen") ||
            lower.contains("component") ||
            lower.contains(".ui.") ||
            lower.contains(".presentation.") -> "Compose UI"
        lower.contains("util") || lower.contains("utils") || lower.contains("policy") -> "Utility"
        else -> "분류 필요"
    }
}

fun isCoreQualityLayer(layer: String): Boolean = layer in coreQualityLayers

fun isUiCoverageLayer(layer: String): Boolean = layer in uiCoverageLayers

fun summarizeCoverageLayers(areas: List<CoverageArea>, layers: List<String>): List<CoverageLayerSummary> {
    return layers.mapNotNull { layer ->
        val layerAreas = areas.filter { it.layer == layer }
        if (layerAreas.isEmpty()) {
            null
        } else {
            CoverageLayerSummary(
                layer = layer,
                missed = layerAreas.sumOf { it.missed },
                covered = layerAreas.sumOf { it.covered },
                zeroCoverageCount = layerAreas.count { it.covered == 0 && it.missed + it.covered > 0 }
            )
        }
    }
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

fun githubPullRequestNumber(): String {
    val githubRef = System.getenv("GITHUB_REF").orEmpty()
    val match = Regex("""refs/pull/(\d+)/""").find(githubRef)
    return match?.groupValues?.get(1) ?: "정보 없음"
}

fun reportContextRows(): List<Pair<String, String>> {
    val branch = System.getenv("GITHUB_HEAD_REF")
        ?: System.getenv("GITHUB_REF_NAME")
        ?: "local"
    val commit = System.getenv("GITHUB_SHA") ?: "local"
    val comparedWith = System.getenv("GITHUB_BASE_REF") ?: "정보 없음"

    return listOf(
        "Repository" to (System.getenv("GITHUB_REPOSITORY") ?: "ksubin-dev/Leaf"),
        "Branch" to branch,
        "Commit" to commit,
        "Pull Request" to githubPullRequestNumber(),
        "Generated At" to Instant.now().toString(),
        "Test Scope" to "JVM and Android debug unit tests",
        "Compared With" to comparedWith,
        "Coverage Exclusions" to "Android generated classes, test classes, Hilt/Dagger generated classes, Compose singleton/preview classes"
    )
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

fun StringBuilder.appendCoverageAreaTable(areas: List<CoverageArea>) {
    appendLine("| 계층 | 클래스 | 라인 커버리지 | 커버된 라인 | 누락 라인 |")
    appendLine("|---|---|---:|---:|---:|")

    if (areas.isEmpty()) {
        appendLine("| 데이터 없음 | 라인 커버리지 데이터 없음 | - | - | - |")
    } else {
        areas.forEach { area ->
            appendLine(
                "| ${area.layer} | `${area.name}` | ${formatCoverage(area.coverage)} | ${area.covered} | ${area.missed} |"
            )
        }
    }
}

fun StringBuilder.appendLayerSummaryTable(summaries: List<CoverageLayerSummary>) {
    appendLine("| 계층 | 라인 커버리지 | 커버된 라인 | 누락 라인 | 0% 클래스 |")
    appendLine("|---|---:|---:|---:|---:|")

    if (summaries.isEmpty()) {
        appendLine("| 데이터 없음 | - | - | - | - |")
    } else {
        summaries.forEach { summary ->
            appendLine(
                "| ${summary.layer} | ${formatCoverage(summary.coverage)} | ${summary.covered} | ${summary.missed} | ${summary.zeroCoverageCount} |"
            )
        }
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

        val coverageAreas = rows.mapNotNull { row ->
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
                    layer = classifyCoverageLayer(name),
                    missed = missed,
                    covered = covered
                )
            }
        }

        val lowCoverageRows = coverageAreas.sortedWith(
            compareBy<CoverageArea> { it.coverage }
                .thenByDescending { it.missed }
                .thenBy { it.name }
        ).take(10)

        val coreQualityAreas = coverageAreas.filter { isCoreQualityLayer(it.layer) }
        val uiCoverageAreas = coverageAreas.filter { isUiCoverageLayer(it.layer) }
        val coreQualitySummaries = summarizeCoverageLayers(coreQualityAreas, coreQualityLayers)
        val uiCoverageSummaries = summarizeCoverageLayers(uiCoverageAreas, uiCoverageLayers)
        val lowCoreQualityRows = coreQualityAreas.sortedWith(
            compareBy<CoverageArea> { it.coverage }
                .thenByDescending { it.missed }
                .thenBy { it.name }
        ).take(10)
        val lowUiCoverageRows = uiCoverageAreas.sortedWith(
            compareBy<CoverageArea> { it.coverage }
                .thenByDescending { it.missed }
                .thenBy { it.name }
        ).take(10)

        val zeroCoverageCount = coverageAreas.count {
            it.missed + it.covered > 0 && it.covered == 0
        }
        val coreZeroCoverageCount = coreQualityAreas.count { it.missed + it.covered > 0 && it.covered == 0 }
        val uiZeroCoverageCount = uiCoverageAreas.count { it.missed + it.covered > 0 && it.covered == 0 }

        val markdown = buildString {
            appendLine("## Report Context")
            appendLine()
            appendLine("| 항목 | 값 |")
            appendLine("|---|---|")
            reportContextRows().forEach { (label, value) ->
                appendLine("| $label | $value |")
            }
            appendLine()
            appendLine("## 커버리지 요약")
            appendLine()
            appendLine("| 지표 | 커버리지 | 커버됨 | 누락 |")
            appendLine("|---|---:|---:|---:|")

            metrics.forEach { (label, prefix) ->
                val missed = rows.sumOf { it.metric("${prefix}_MISSED") }
                val covered = rows.sumOf { it.metric("${prefix}_COVERED") }
                appendLine("| $label | ${formatCoverage(coveragePercent(missed, covered))} | $covered | $missed |")
            }

            appendLine()
            appendLine("## 커버리지가 낮은 영역")
            appendLine()
            appendLine("- 라인 커버리지 0% 클래스 수: $zeroCoverageCount")
            appendLine("- 핵심 품질 계층 0% 클래스 수: $coreZeroCoverageCount")
            appendLine("- UI/Compose 별도 검토 계층 0% 클래스 수: $uiZeroCoverageCount")
            appendLine("- Room/Hilt/Compose generated helper class는 목록에서 제외했습니다.")
            appendLine()
            appendCoverageAreaTable(lowCoverageRows)

            appendLine()
            appendLine("## 핵심 품질 계층 커버리지 요약")
            appendLine()
            appendLine("Worker, Repository, DataSource, Mapper, ViewModel, UseCase 등 저장/업로드 안정성과 직접 연결되는 계층을 전체 커버리지와 분리해 표시합니다.")
            appendLine()
            appendLayerSummaryTable(coreQualitySummaries)

            appendLine()
            appendLine("## 핵심 품질 계층 낮은 영역")
            appendLine()
            appendLine("- 라인 커버리지 오름차순, 누락 라인 내림차순 기준 상위 10개입니다.")
            appendLine("- AI 분석과 다음 테스트 후보 산정에서는 이 영역을 우선 입력으로 사용합니다.")
            appendLine()
            appendCoverageAreaTable(lowCoreQualityRows)

            appendLine()
            appendLine("## UI/Compose 별도 검토 영역")
            appendLine()
            appendLine("- 단순 렌더링, Screen, Navigation, Component 코드는 핵심 품질 계층과 분리해 해석합니다.")
            appendLine("- 입력 검증, 저장/삭제 트리거, 복잡한 상태 분기, 장애 이력이 확인된 UI는 별도 테스트 후보로 다시 검토합니다.")
            appendLine()
            appendLayerSummaryTable(uiCoverageSummaries)
            appendLine()
            appendCoverageAreaTable(lowUiCoverageRows)

            appendLine()
            appendLine("## AI 분석 입력 요약")
            appendLine()
            appendLine("- 전체 커버리지는 앱 전체 자동 검증 수준을 보는 참고 지표입니다.")
            appendLine("- 테스트 우선순위 판단은 `핵심 품질 계층 커버리지 요약`과 `핵심 품질 계층 낮은 영역`을 먼저 사용합니다.")
            appendLine("- `UI/Compose 별도 검토 영역`은 단순 UI 렌더링으로 보류하되, 사용자 입력 검증이나 저장/삭제 트리거가 확인되면 다시 검토합니다.")
            appendLine("- 낮은 커버리지만으로 버그를 단정하지 않고, 사용자 데이터 영향과 실패 복구 가능성을 함께 봅니다.")

            appendLine()
            appendLine("## 리포트 파일")
            appendLine()
            appendLine("- HTML 리포트 artifact: `jacoco-html-report`")
            appendLine("- Markdown 요약 artifact: `jacoco-coverage-summary`")
            appendLine("- 원본 CSV: `build/reports/jacoco/jacocoTestReport/jacocoTestReport.csv`")
        }

        val summaryFile = summaryReport.get().asFile
        summaryFile.parentFile.mkdirs()
        summaryFile.writeText(markdown)
        logger.lifecycle("JaCoCo coverage summary generated: ${summaryFile.absolutePath}")
    }
}

tasks.register<Exec>("generateCustomCoverageReport") {
    group = "verification"
    description = "Generates a shareable custom HTML report from the JaCoCo coverage summary."

    dependsOn(tasks.named("jacocoCoverageSummary"))

    val summaryReport = layout.buildDirectory.file("reports/jacoco/jacocoTestReport/coverage-summary.md")
    val csvReport = layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.csv")
    val aiAnalysisReport = layout.projectDirectory.file("docs/ai-coverage-analysis-result.json")
    val outputDirectory = layout.buildDirectory.dir("reports/leafy-test-report")

    inputs.file(summaryReport)
    inputs.file(csvReport)
    if (aiAnalysisReport.asFile.exists()) {
        inputs.file(aiAnalysisReport)
    }
    inputs.file(layout.projectDirectory.file("scripts/generate-coverage-report.mjs"))
    outputs.dir(outputDirectory)

    commandLine(
        "node",
        "scripts/generate-coverage-report.mjs",
        "--summary",
        summaryReport.get().asFile.absolutePath,
        "--csv",
        csvReport.get().asFile.absolutePath,
        "--ai-json",
        aiAnalysisReport.asFile.absolutePath,
        "--out",
        outputDirectory.get().asFile.absolutePath
    )
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
