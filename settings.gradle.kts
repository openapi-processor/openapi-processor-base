plugins {
    id("com.gradle.enterprise") version("3.14.1")
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
