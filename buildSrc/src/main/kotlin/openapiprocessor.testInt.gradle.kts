@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
//    jacoco
    kotlin
//    id("org.barfuin.gradle.jacocolog")
}

// see buildSrc/build.gradle.kts
val libs = the<LibrariesForLibs>()

testing {
    suites {
        val test by getting(JvmTestSuite::class)

        val testInt by registering(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                implementation(project())
            }

            sources {
                java {
                    srcDirs("src/testInt/kotlin")
                }
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("testInt"))
}
//
//jacoco {
//    toolVersion = libs.versions.jacoco.get()
//}
//
//tasks.named<JacocoReport>("jacocoTestReport") {
//    reports {
//        xml.required.set(true)
//        csv.required.set(false)
//        html.required.set(false)
//        //html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
//    }
//}
