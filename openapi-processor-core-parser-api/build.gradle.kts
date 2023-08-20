plugins {
    id("openapiprocessor.library")
    id("openapiprocessor.publish")
}

repositories {
}

dependencies {
}

publishing {
    publications {
        getByName<MavenPublication>("openapiprocessor") {
            pom {
                description.set("OpenAPI Processor Parser API")
            }
        }
    }
}
