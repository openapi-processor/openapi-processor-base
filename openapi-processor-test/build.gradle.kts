@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed

plugins {
    id("openapiprocessor.library")
    id("openapiprocessor.publish")

    groovy
}

repositories {
}

dependencies {
    compileOnly(libs.openapi.processor.api)

    implementation(platform(libs.jackson.bom))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    implementation(platform(libs.groovy.bom))
    implementation("org.apache.groovy:groovy")
    implementation("org.apache.groovy:groovy-nio")

    implementation(libs.diff.utils)
    implementation(libs.jimfs) {
        exclude("com.google.guava")
    }
    implementation(libs.slf4j)
}

publishing {
    publications {
        getByName<MavenPublication>("openapiprocessor") {
            pom {
                description.set("OpenAPI Processor Test")
            }
        }
    }
}
