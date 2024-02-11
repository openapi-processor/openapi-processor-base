plugins {
    java
    alias(libs.plugins.jacoco)
    alias(libs.plugins.nexus)
}

group = "io.openapiprocessor"
version = libs.versions.processor.get()
println("version: $version")

repositories {
    mavenCentral()
}

// do not create jar for the root project
tasks.named("jar") { enabled = false }


tasks.named("jacocoLogAggregatedCoverage") {
    dependsOn ("check")
}

tasks.named("build") {
    dependsOn ("jacocoLogAggregatedCoverage")
}

extra["publishUser"] = buildProperty("PUBLISH_USER")
extra["publishKey"] = buildProperty("PUBLISH_KEY")
val publishUser: String by extra
val publishKey: String by extra

nexusPublishing {
    this.repositories {
        sonatype {
            username.set(publishUser)
            password.set(publishKey)
        }
    }
}

//fun environment(key: String): Provider<String> = providers.environmentVariable(key)

val key: String? = System.getenv("SIGN_KEY")
println("#1# (${key?.substring(0, 200)})")

val keyx: String = buildProperty("SIGN_KEY")
println("#2# (${keyx.substring(0, 200)})")

//val key2 = environment("SIGN_KEY").get()
//println("### (${key2.substring(0, 200)})")

val SIGN_KEY_ORG: String? by project
println("#3# (${SIGN_KEY_ORG?.substring(0, 200)})")

