plugins {
    base
    alias(build.plugins.openapiprocessor.publish)
    id("openapiprocessor.versions")
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
