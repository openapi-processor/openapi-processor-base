import org.gradle.accessors.dm.LibrariesForBuild
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`
    kotlin
}

// see buildSrc/build.gradle.kts
val libs = the<LibrariesForLibs>()
val build = the<LibrariesForBuild>()

group = "io.openapiprocessor"
version = libs.versions.processor.get()
println("version: $version")

java {
    withJavadocJar ()
    withSourcesJar ()
}

tasks.javadoc {
    exclude("**/io/openapiprocessor/core/processor/mapping/v2/parser/antlr/**")
}

kotlin {
    jvmToolchain(build.versions.build.jdk.get().toInt())
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
//    checkerFramework(libs.checker)
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
        languageVersion.set(JavaLanguageVersion.of(build.versions.test.jdk.get()))
    })
}

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
