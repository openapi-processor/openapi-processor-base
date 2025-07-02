import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("maven-publish")
    id("signing")
}

// see buildSrc/build.gradle catalog hack
val libs = the<LibrariesForLibs>()

fun properties(key: String): Provider<String> = providers.gradleProperty(key)
val componentName = if (isPlatform()) { "javaPlatform" } else { "java" }

publishing {
    publications {
        create<MavenPublication>("openapiprocessor") {
            from(components[componentName])

            pom {
                group = "io.openapiprocessor"
                version = libs.versions.processor.get()

                name.set("openapi-processor")
                description.set("OpenAPI Processor")
                url.set("https://openapiprocessor.io")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("hauner")
                        name.set("Martin Hauner")
                    }
                }

                scm {
                   url.set("https://github.com/openapi-processor/openapi-processor-base")
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://ossrh-staging-api.central.sonatype.com/content/repositories/snapshots"
            url = uri(if (isReleaseVersion()) releasesRepoUrl else snapshotsRepoUrl)

            credentials {
                username = buildProperty("PUBLISH_USER")
                password = buildProperty("PUBLISH_KEY")
            }
        }
    }
}

// signing requires the sign key and pwd as environment variables:
//
// ORG_GRADLE_PROJECT_signKey=...
// ORG_GRADLE_PROJECT_signPwd=...

signing {
    setRequired({ gradle.taskGraph.hasTask("${project.path}:publishToSonatype") })

    val signKey: String? by project
    val signPwd: String? by project
    useInMemoryPgpKeys(signKey, signPwd)

    sign(publishing.publications["openapiprocessor"])
}
