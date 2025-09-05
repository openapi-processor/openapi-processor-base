plugins {
    id("openapiprocessor.test")
    id("openapiprocessor.library")
    id("openapiprocessor.publish")
}

publishing {
    publications {
        getByName<MavenPublication>("openapiprocessor") {
            pom {
                description = "OpenAPI Processor Parser API"
            }
        }
    }
}

dependencies {
    testImplementation (platform(libs.kotest.bom))
    testImplementation (libs.kotest.runner)
    testImplementation (libs.mockk)
}
