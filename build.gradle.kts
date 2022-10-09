@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

plugins {
    java
    alias(libs.plugins.jacoco)
    alias(libs.plugins.nexus)
}

// do not create jar for the root project
tasks.named("jar") { enabled = false }

repositories {
    mavenCentral()
}

// check
tasks.named("build") {
    dependsOn ("jacocoLogAggregatedCoverage")
}

tasks.named("jacocoLogAggregatedCoverage") {
    dependsOn ("check")
}

group = "io.openapiprocessor"
version = libs.versions.processor.get()
println("version: $version")

extra["publishUser"] = buildProperty("PUBLISH_USER")
extra["publishKey"] = buildProperty("PUBLISH_KEY")
val publishUser: String by extra
val publishKey: String by extra

nexusPublishing {
    repositories {
        sonatype {
            username.set(publishUser)
            password.set(publishKey)
        }
    }
}
