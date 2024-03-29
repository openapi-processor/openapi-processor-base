import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
//    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    // catalog hack: https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.checker)
    implementation(libs.plugin.outdated)
}

gradlePlugin {
    plugins {
        create("VersionPlugin") {
            id = "openapiprocessor.version"
            implementationClass = "VersionPlugin"
        }
    }
}
