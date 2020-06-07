plugins {
    groovy
    id("java-library")
    id("maven-publish")
    id("com.jfrog.bintray") version ("1.8.5")
    id("com.github.ben-manes.versions") version ("0.28.0")
}

group = "com.github.hauner.openapi"
version = "1.0.0.M1"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

ext {
    set("processorApiVersion", "1.0.0.M4")

    set("bintrayUser", project.findProperty("BINTRAY_USER") ?: "n/a")
    set("bintrayKey", project.findProperty("BINTRAY_KEY") ?: "n/a")
}

repositories {
    mavenCentral()
    maven {
        setUrl("https://dl.bintray.com/hauner/openapi-processor")
    }
}

dependencies {
    compileOnly("com.github.hauner.openapi:openapi-processor-api:${project.ext.get("processorApiVersion")}")

    implementation("org.codehaus.groovy:groovy:2.5.12")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.0")
    implementation("io.github.java-diff-utils:java-diff-utils:4.7")
    implementation("com.google.jimfs:jimfs:1.1")

    implementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    runtimeOnly("'org.junit.vintage:junit-vintage-engine:5.6.2")
    compileOnly("junit:junit:4.13")
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

bintray {
    user = project.ext.get("bintrayUser").toString()
    key = project.ext.get("bintrayKey").toString()

    setPublications("processor")

    pkg.apply {
        repo = "openapi-processor"
        name = "openapi-processor-test"
        //userOrg = 'openapi-processor'
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/hauner/openapi-processor-test"

        version.apply {
            name = project.version.toString()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("processor") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}
