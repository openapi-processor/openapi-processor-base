plugins {
    id("openapiprocessor.library")
    id("openapiprocessor.publish")
    id("openapiprocessor.version")

    java
}

dependencies {

}

tasks {
    generateVersion {
        targetPackage.set("io.openapiprocessor")
    }
}

publishing {
    publications {
        getByName<MavenPublication>("openapiprocessor") {
            pom {
                description.set("OpenAPI Processor Annotations")
            }
        }
    }
}
