plugins {
    `maven-publish`
    signing
   id("io.openapiprocessor.build.plugin.publish")
}

publishing {
    publications {
        create<MavenPublication>("openapiprocessor") {
            from(components["java"])
        }
    }
}

publishingCentral {
    stagingDir = rootProject.layout.buildDirectory.dir("staging")
    deploymentDir = rootProject.layout.buildDirectory.dir("deployment")
    deploymentName = "base"
    waitFor = "VALIDATED"
}
