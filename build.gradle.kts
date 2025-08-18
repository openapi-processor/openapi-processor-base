plugins {
    java
    alias(libs.plugins.jacoco)
}

group = "io.openapiprocessor"
version = libs.versions.processor.get()
println("version: $version")

// do not create jar for the root project
tasks.named("jar") { enabled = false }


tasks.named("jacocoLogAggregatedCoverage") {
    dependsOn ("check")
}

tasks.named("build") {
    dependsOn ("jacocoLogAggregatedCoverage")
}
