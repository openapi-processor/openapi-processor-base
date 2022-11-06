plugins {
    id("openapiprocessor.library")
    id("openapiprocessor.version")

    java
}

dependencies {
    api (libs.slf4j)
}

tasks {
    generateVersion {
        targetPackage.set("io.openapiprocessor.slf4j")
    }
}
