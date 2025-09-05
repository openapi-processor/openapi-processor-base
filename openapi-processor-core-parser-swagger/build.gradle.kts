plugins {
    id("openapiprocessor.library")
    id("openapiprocessor.publish")
    `java-test-fixtures`
}

repositories {
}

dependencies {
    implementation (project(":openapi-processor-core-parser-api"))

    implementation (libs.swagger) {
        exclude(group = "io.swagger.parser.v3", module = "swagger-parser-v2-converter")
        exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
    }
    implementation (libs.uritemplate)

    testImplementation (project(":openapi-processor-test"))
    testImplementation (platform(libs.kotest.bom))
    testImplementation (libs.kotest.runner)
    testImplementation (libs.mockk)
    testImplementation (libs.logback)

    testFixturesImplementation (project(":openapi-processor-core-parser-api"))
    testFixturesImplementation (project(":openapi-processor-test"))
    testFixturesImplementation (libs.swagger) {
        exclude(group = "io.swagger.parser.v3", module = "swagger-parser-v2-converter")
        exclude(group = "io.swagger.core.v3", module = "swagger-annotations")
    }

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
                description = "OpenAPI Parser swagger"
            }
        }
    }
}
