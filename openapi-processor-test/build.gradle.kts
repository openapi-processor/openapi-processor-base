plugins {
    groovy
    id("java-library")
    id("maven-publish")
    id("com.github.ben-manes.versions") version ("0.36.0")
}

val projectGroupId: String by project
val projectVersion: String by project

group = projectGroupId
version = projectVersion

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

ext {
    set("bintrayUser", project.findProperty("BINTRAY_USER") ?: "n/a")
    set("bintrayKey", project.findProperty("BINTRAY_KEY") ?: "n/a")
}

repositories {
    mavenCentral()

    maven {
        setUrl("https://dl.bintray.com/openapi-processor/primary")
        content {
           includeGroup ("io.openapiprocessor")
        }
        mavenContent {
            releasesOnly()
        }
    }

    maven {
        setUrl("https://oss.jfrog.org/artifactory/oss-snapshot-local")
        content {
           includeGroup("io.openapiprocessor")
        }
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    compileOnly("io.openapiprocessor:openapi-processor-api:1.2.0")

    implementation("org.codehaus.groovy:groovy:2.5.12")
    implementation("org.codehaus.groovy:groovy-nio:2.5.12")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.0")
    implementation("io.github.java-diff-utils:java-diff-utils:4.9")
    implementation("com.google.jimfs:jimfs:1.1") {
        exclude("com.google.guava")
    }

    compileOnly("junit:junit:4.13.1")
    implementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    runtimeOnly("org.junit.vintage:junit-vintage-engine:5.7.0")
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
val bintrayUser: String by project.ext
val bintrayKey: String by project.ext

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
                description.set("${projectTitle} - ${projectDesc} - ${project.name} module")
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
            val releasesRepoUrl = "https://api.bintray.com/maven/openapi-processor/primary/${project.name}/;publish=1;override=0"
            val snapshotsRepoUrl = "https://oss.jfrog.org/oss-snapshot-local/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

            credentials {
                username = project.ext.get("bintrayUser").toString()
                password = project.ext.get("bintrayKey").toString()
            }
        }
    }
}
