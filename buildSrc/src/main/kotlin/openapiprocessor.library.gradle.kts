import com.github.benmanes.gradle.versions.reporter.PlainTextReporter
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.withType

plugins {
    `java-library`
    jacoco

    id("org.jetbrains.kotlin.jvm")
    id("com.github.ben-manes.versions")
}

// see buildSrc/build.gradle.kts
val libs = the<LibrariesForLibs>()

group = "io.openapiprocessor"
version = libs.versions.processor.get()
println("version: $version")

java {
    withJavadocJar ()
    withSourcesJar ()
}

kotlin {
    jvmToolchain(libs.versions.build.jdk.get().toInt())
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
//    checkerFramework(libs.checker)
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(false)
        //html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs(listOf(
        "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
    ))

    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.test.jdk.get()))
    })

    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        println("candidate $candidate")
        candidate.version.isNonStable()
    }

    outputFormatter {
        exceeded.dependencies.removeIf { d -> ignore.contains("${d.group}:${d.name}") }

        val plainTextReporter = PlainTextReporter(
            project,
            revision,
            gradleReleaseChannel
        )
        plainTextReporter.write(System.out, this)
    }
}


fun String.isNonStable(): Boolean {
    val nonStable = listOf(
        ".M[0-9]+$",
        ".RC[0-9]*$",
        ".alpha.?[0-9]+$",
        ".beta.?[0-9]+$",
    )

    for (n in nonStable) {
       if (this.contains("(?i)$n".toRegex())) {
           println("not stable: $this")
           return true
       }
    }

    return false
}

val ignore = listOf(
    "org.checkerframework:jdk8"
)

/*
configure<CheckerFrameworkExtension> {
    skipCheckerFramework = true
    excludeTests = true
    extraJavacArgs = listOf("-Awarns")

    checkers = listOf(
        "org.checkerframework.checker.nullness.NullnessChecker",
//        "org.checkerframework.checker.interning.InterningChecker",
//        "org.checkerframework.checker.resourceleak.ResourceLeakChecker",
//        "org.checkerframework.checker.index.IndexChecker"
    )
}
 */
