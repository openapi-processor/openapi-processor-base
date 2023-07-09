plugins {
    id("openapiprocessor.library")
    id("openapiprocessor.publish")
    id("org.jetbrains.kotlin.jvm")
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
