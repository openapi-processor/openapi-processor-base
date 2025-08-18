plugins {
    id("com.gradle.enterprise") version("3.14.1")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}

gradleEnterprise {
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}

rootProject.name = "openapi-processor-base"

include("openapi-processor-core")
include("openapi-processor-core-parser-api")
//include("openapi-processor-core-parser-internal")
include("openapi-processor-core-parser-swagger")
include("openapi-processor-core-parser-openapi4j")
include("openapi-processor-test")
include("openapi-processor-test-api")

System.setProperty("sonar.gradle.skipCompile", "true")
