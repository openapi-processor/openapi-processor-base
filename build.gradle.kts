plugins {
    base
    alias(build.plugins.kotlin.jvm) apply false
    alias(build.plugins.openapiprocessor.publish)
    id("jacoco-report-aggregation")
}

group = "io.openapiprocessor"
version = libs.versions.processor.get()
println("version: $version")

publishingCentral {
    aggregateSubProjects = true
    stagingDir = layout.buildDirectory.dir("staging")
    deploymentDir = layout.buildDirectory.dir("deployment")
    deploymentName = "base"
    waitFor = "VALIDATED"
}

jacoco {
    toolVersion = build.versions.jacoco.get()
}

tasks.named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
    rejectVersionIf {
        val rejected = candidate.version.isNonStable()
        if (rejected) {
            println("candidate: $candidate -> rejected")
        }
        rejected
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
           //println("not stable: $this")
           return true
       }
    }

    return false
}
