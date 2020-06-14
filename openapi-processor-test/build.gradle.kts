plugins {
    groovy
    id("java-library")
    id("maven-publish")
    id("com.jfrog.bintray") version ("1.8.5")
    id("com.jfrog.artifactory") version ("4.15.2")
    id("com.github.ben-manes.versions") version ("0.28.0")
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
    }
    maven {
        // deprecated but still used
        setUrl("https://dl.bintray.com/hauner/openapi-processor")
    }
}

dependencies {
    compileOnly("com.github.hauner.openapi:openapi-processor-api:1.0.0")

    implementation("org.codehaus.groovy:groovy:2.5.12")
    implementation("org.codehaus.groovy:groovy-nio:2.5.12")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.0")
    implementation("io.github.java-diff-utils:java-diff-utils:4.7")
    implementation("com.google.jimfs:jimfs:1.1") {
        exclude("com.google.guava")
    }

    compileOnly("junit:junit:4.13")
    implementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    runtimeOnly("org.junit.vintage:junit-vintage-engine:5.6.2")
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
        create<MavenPublication>("projectArtifacts") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            with(pom) {

                withXml {
                    val root = asNode()
                    root.appendNode("name", projectTitle)
                    root.appendNode("description", projectDesc)
                    root.appendNode("url", projectUrl)
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
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
}


artifactory {
    setContextUrl("https://oss.jfrog.org")

    publish(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
        repository(delegateClosureOf<groovy.lang.GroovyObject> {
            setProperty("repoKey", "oss-snapshot-local")
            setProperty("username", bintrayUser)
            setProperty("password", bintrayKey)
        })

        defaults(delegateClosureOf<groovy.lang.GroovyObject> {
            invokeMethod("publications", arrayOf("projectArtifacts"))
            setProperty("publishArtifacts", true)
            setPublishPom(true)
        })
    })

    resolve(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig> {
        setProperty("repoKey", "jcenter")
    })
}


bintray {
    user = project.ext.get("bintrayUser").toString()
    key = project.ext.get("bintrayKey").toString()

    setPublications("projectArtifacts")

    pkg.apply {
        repo = "primary"
        name = "openapi-processor-test"
        userOrg = "openapi-processor"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/${projectGithubRepo}"

        version.apply {
            name = project.version.toString()
        }
    }
}
