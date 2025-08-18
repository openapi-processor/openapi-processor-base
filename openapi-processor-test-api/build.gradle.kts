plugins {
    id("openapiprocessor.library")
    id("openapiprocessor.publish")
    groovy
}

publishing {
    publications {
        getByName<MavenPublication>("openapiprocessor") {
            pom {
                description = "OpenAPI Processor Test Api"
            }
        }
    }
}
