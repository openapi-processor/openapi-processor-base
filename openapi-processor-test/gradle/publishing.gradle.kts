/*
val projectTitle: String by project
val projectDesc: String by project
val projectUrl: String by project
val projectGithubRepo: String by project

// does not work on oss.sonatype.org
tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("OpenApiProcessor") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                name.set(projectTitle)
                description.set(projectDesc)
                url.set(projectUrl)

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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
                   url.set("https://github.com/${projectGithubRepo}")
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
                username = project.extra["publishUser"].toString()
                password = project.extra["publishKey"].toString()
            }
        }
    }
}

//tasks.withType<Sign>().configureEach {
//    onlyIf { isReleaseVersion() }
//}

signing {
    useInMemoryPgpKeys(
        project.extra["signKey"].toString(),
        project.extra["signPwd"].toString())

    sign(publishing.publications["OpenApiProcessor"])
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(project.extra["publishUser"].toString())
            password.set(project.extra["publishKey"].toString())
        }
    }
}
*/
