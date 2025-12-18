plugins {
    base
    alias(libs.plugins.jacoco)
    id("io.openapiprocessor.build.plugin.publish")
}

// check
//tasks.named("build") {
//    dependsOn ("jacocoLogAggregatedCoverage")
//}

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
