plugins {
    id("openapiprocessor.library")
    id("openapiprocessor.publish")
    id("openapiprocessor.test")
    `java-test-fixtures`
}

dependencies {
    implementation (project(":openapi-processor-core-parser-api"))
    implementation (libs.openapi4j)
    implementation (libs.uritemplate)
    implementation (libs.slf4j)

    testImplementation (project(":openapi-processor-test"))
    testImplementation (platform(libs.kotest.bom))
    testImplementation (libs.kotest.runner)
    testImplementation (libs.kotest.datatest)
    testImplementation (libs.mockk)
    testImplementation (libs.jimfs)
    testImplementation (libs.logback)

    testFixturesImplementation (project(":openapi-processor-core-parser-api"))
    testFixturesImplementation (project(":openapi-processor-test"))
    testFixturesImplementation (libs.openapi4j)

    constraints {
        implementation(libs.jackson.bom) { because("use same jackson") }

        testImplementation(libs.junit.bom) { because("use same junit") }
        testImplementation(libs.jackson.bom) { because("use same jackson") }

        testFixturesImplementation(libs.junit.bom) { because("use same junit") }
        testFixturesImplementation(libs.jackson.bom) { because("use same jackson") }
    }
}

publishing {
    publications {
        getByName<MavenPublication>("openapiprocessor") {
            pom {
                description = "OpenAPI Parser openapi4j"
            }
        }
    }
}
