import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    jacoco
    id("org.barfuin.gradle.jacocolog")
}

// see buildSrc/build.gradle.kts
val libs = the<LibrariesForLibs>()

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.withType<JacocoReport>().configureEach {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(false)
        //html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }

    val execFiles = files(tasks.withType<Test>().map { testTask ->
        testTask.extensions.getByType<JacocoTaskExtension>().destinationFile
    })

    executionData(execFiles)

    val mainSourceSet = project.extensions.getByType<SourceSetContainer>().getByName("main")
    sourceSets(mainSourceSet)
}
