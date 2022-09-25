plugins {
    groovy
    id("java-library")
    id("maven-publish")
    id("signing")
    alias(libs.plugins.nexus)
    alias(libs.plugins.versions)
}

val projectGroupId: String by project
val projectVersion: String by project

group = projectGroupId
version = projectVersion

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

fun getBuildProperty(property: String): String {
    val prop: String? = project.findProperty(property) as String?
    if(prop != null) {
        return prop
    }

    val env: String? = System.getenv(property)
    if (env != null) {
        return env
    }

    return "n/a"
}

fun getBuildSignKey(property: String): String {
    val prop: String? = project.findProperty(property) as String?
    if(prop != null) {
        return prop
    }

    val env: String? = System.getenv(property)
    if (env != null) {
        return env.replace("\\n", "\n")
    }

    return "n/a"
}

fun isReleaseVersion(): Boolean {
    return !(project.version.toString().endsWith("SNAPSHOT"))
}

ext {
    set("publishUser", getBuildProperty("PUBLISH_USER"))
    set("publishKey", getBuildProperty("PUBLISH_KEY"))
    set("signKey", getBuildSignKey("SIGN_KEY"))
    set("signPwd", getBuildProperty("SIGN_PWD"))
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://oss.sonatype.org/content/repositories/snapshots")
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    compileOnly(libs.openapi.processor.api)

    implementation(platform(libs.jackson.bom))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    implementation(platform(libs.groovy.bom))
    implementation("org.codehaus.groovy:groovy")
    implementation("org.codehaus.groovy:groovy-nio")

    implementation(libs.diff.utils)
    implementation(libs.jimfs) {
        exclude("com.google.guava")
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("javadoc")
    archiveClassifier.set("javadoc")
    from(tasks.javadoc.get().destinationDir)
}

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}

//apply(from = "${rootProject.rootDir}/gradle/publishing.gradle.kts")

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
