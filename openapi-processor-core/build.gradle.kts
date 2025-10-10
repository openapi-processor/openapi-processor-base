plugins {
    antlr
    groovy
    kotlin
    id("openapiprocessor.library")
    id("openapiprocessor.test")
    id("openapiprocessor.testInt")
    id("openapiprocessor.publish")
    id("openapiprocessor.newapi")
    alias(libs.plugins.versions)
    alias(libs.plugins.sonar)
    id("jacoco-report-aggregation")
    alias(libs.plugins.jacoco)
}

versions {
    packageName = "io.openapiprocessor.core"
    entries.putAll(mapOf(
        "version" to libs.versions.processor.get()
    ))
}

sourceSets {
    main {
        java {
            srcDir(tasks.named("generateGrammarSource"))
        }
    }

    test {
        java {
            srcDir(tasks.named("generateTestGrammarSource"))
        }
//        groovy {
//            srcDir(tasks.named("compileKotlin"))
//            srcDir(tasks.named("compileTestKotlin"))
//        }
    }

    testInt {
        java {
            srcDir(tasks.named("generateTestIntGrammarSource"))
        }
    }
}

tasks.compileTestGroovy {
    classpath += sourceSets.main.get().compileClasspath
    classpath += files(tasks.compileKotlin.get().destinationDirectory)
    classpath += files(tasks.compileTestKotlin.get().destinationDirectory)
}

tasks.named<AntlrTask>("generateGrammarSource") {
    val antlrPkg = "io.openapiprocessor.core.processor.mapping.v2.parser.antlr"
    arguments = arguments + listOf("-package", antlrPkg)
    outputDirectory = layout.buildDirectory.dir("antlr/${antlrPkg.replace('.', '/')}").get().asFile
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    antlr (libs.antlr)

    compileOnly (libs.openapi.processor.api)
    compileOnly (libs.logback)

    implementation (project(":openapi-processor-test-api"))
    implementation (project(":openapi-processor-core-parser-api"))
    implementation (platform(libs.openapi.parser.bom))
    implementation (libs.openapi.parser.parser)
    implementation (libs.io.jackson)

    implementation (platform(libs.jackson.bom))
    implementation (libs.jackson.kotlin)
    implementation (libs.jackson.yaml)
    implementation (libs.uritemplate)
    implementation (libs.commons.text)
    implementation (libs.commonmark)
    implementation (libs.format.java.google)
    implementation (libs.format.java.eclipse)
    implementation (libs.slf4j)

    testImplementation (project(":openapi-processor-core-parser-api"))
    testImplementation (project(":openapi-processor-core-parser-swagger"))
    testImplementation (project(":openapi-processor-core-parser-openapi4j"))
    testImplementation (testFixtures(project(":openapi-processor-core-parser-swagger")))
    testImplementation (testFixtures(project(":openapi-processor-core-parser-openapi4j")))
    testImplementation (project(":openapi-processor-test"))
    testImplementation (libs.openapi.processor.api)
    testImplementation (platform(libs.groovy.bom))
    testImplementation (platform(libs.kotest.bom))
    testImplementation (libs.kotest.runner)
    testImplementation (libs.kotest.table)
    testImplementation (libs.mockk)
    testImplementation (libs.spock)
    testImplementation (libs.logback)
    testImplementation (libs.jimfs)

    testIntImplementation (project(":openapi-processor-core-parser-api"))
    testIntImplementation (project(":openapi-processor-core-parser-swagger"))
    testIntImplementation (project(":openapi-processor-core-parser-openapi4j"))
    testIntImplementation (testFixtures(project(":openapi-processor-core-parser-swagger")))
    testIntImplementation (testFixtures(project(":openapi-processor-core-parser-openapi4j")))
    testIntImplementation (project(":openapi-processor-test"))
    testIntImplementation (project(":openapi-processor-test-api"))
    testIntImplementation (libs.openapi.processor.api)
    testIntImplementation (platform(libs.groovy.bom))
    testIntImplementation (platform(libs.kotest.bom))
    testIntImplementation (libs.kotest.runner)
    testIntImplementation (libs.kotest.table)
    testIntImplementation (libs.mockk)
    testIntImplementation (libs.spock)
    testIntImplementation (libs.logback)
    testIntImplementation (libs.jimfs)
}

publishing {
    publications {
        named<MavenPublication>("openapiprocessor") {
            pom {
                description = "OpenAPI Processor Core"
            }
        }
    }
}

tasks.jacocoTestReport {
    executionData.from(tasks.named<Test>("test").map<File> {
        it.extensions.getByType(JacocoTaskExtension::class.java).destinationFile as File
    })
    executionData.from(tasks.named<Test>("testInt").map<File> {
        it.extensions.getByType(JacocoTaskExtension::class.java).destinationFile as File
    })

    reports {
        xml.required = true
        html.required = true
    }
}

sonarqube {
  properties {
    property("sonar.projectKey", "openapi-processor_openapi-processor-base-core")
    property("sonar.organization", "openapi-processor")
    property("sonar.host.url", "https://sonarcloud.io")
    property("sonar.coverage.jacoco.xmlReportPaths", layout.buildDirectory.dir("reports/jacoco/test/jacocoTestReport.xml").get().toString())
  }
}
