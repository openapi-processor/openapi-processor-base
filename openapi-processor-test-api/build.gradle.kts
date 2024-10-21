plugins {
    id("openapiprocessor.library")
    id("openapiprocessor.publish")
    groovy
}

dependencies {
}

publishing {
    publications {
        getByName<MavenPublication>("openapiprocessor") {
            pom {
                description.set("OpenAPI Processor Test Api")
            }
        }
    }
}
