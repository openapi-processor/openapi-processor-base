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

fun properties(key: String): Provider<String> = providers.gradleProperty(key)
fun environment(key: String): Provider<String> = providers.environmentVariable(key)

val multiline = environment("MULTILINE")
print("multiline ($multiline)")
val multiline2 = environment("MULTILINE2")
print("multiline2 ($multiline2)")

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
