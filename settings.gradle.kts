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

plugins {
    id("io.github.ben-manes.versions.settings") version("0.61.0")
    id("com.gradle.develocity").version("4.5.0")
}

develocity {
    if (System.getenv("CI") != null) {
        buildScan {
            termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
            termsOfUseAgree.set("yes")
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
