plugins {
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
