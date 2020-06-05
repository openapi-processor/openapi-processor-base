plugins {
    groovy
    id("java-library")
    id("maven-publish")
    id("com.jfrog.bintray") version ("1.8.5")
    id("com.github.ben-manes.versions") version ("0.28.0")
}

group = "com.github.hauner.openapi"
version = "1.0.0.M1"

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

    implementation("org.codehaus.groovy:groovy-all:2.5.10")
    implementation("com.google.jimfs:jimfs:1.1")
    implementation("io.github.java-diff-utils:java-diff-utils:4.5")
}
