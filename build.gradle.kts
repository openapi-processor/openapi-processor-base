plugins {
    base
    id("jacoco-report-aggregation")
    id("io.openapiprocessor.build.plugin.publish")
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
