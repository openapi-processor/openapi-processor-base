plugins {
    id("openapiprocessor.library")
    id("openapiprocessor.publish")
    id("org.jetbrains.kotlin.jvm")
    `java-test-fixtures`
}

repositories {
}

dependencies {
    implementation (project(":openapi-processor-core-parser-api"))
    implementation (libs.openapi4j)
    implementation (libs.slf4j)

    testFixturesImplementation (project(":openapi-processor-core-parser-api"))
    testFixturesImplementation (project(":openapi-processor-test"))
    testFixturesImplementation (libs.openapi4j)

    constraints {
        implementation(libs.jackson.bom) { because("use latest jackson") }
    }
}

publishing {
    publications {
        getByName<MavenPublication>("openapiprocessor") {
            pom {
                description.set("OpenAPI Parser openapi4j")
            }
        }
    }
}
