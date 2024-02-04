import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("maven-publish")
    id("signing")
}

// see buildSrc/build.gradle catalog hack
val libs = the<LibrariesForLibs>()

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
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
            url = uri(if (isReleaseVersion()) releasesRepoUrl else snapshotsRepoUrl)

            credentials {
                username = buildProperty("PUBLISH_USER")
                password = buildProperty("PUBLISH_KEY")
            }
        }
    }
}

//tasks.withType<Sign>().configureEach {
//    onlyIf { isReleaseVersion() }
//}

if (!buildProperty("SKIP_SIGNING").toBoolean()) {

    signing {
        useInMemoryPgpKeys(buildSignKey("SIGN_KEY"), buildProperty("SIGN_PWD"))
        sign(publishing.publications["openapiprocessor"])
    }
}

//nexusStaging {
//    username = publishUser
//    password = publishKey
//}
