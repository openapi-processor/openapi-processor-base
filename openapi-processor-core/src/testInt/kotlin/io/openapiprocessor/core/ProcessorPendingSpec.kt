/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.parser.ParserType.INTERNAL
import io.openapiprocessor.core.parser.ParserType.SWAGGER
import io.openapiprocessor.test.*

/**
 * helper to run selected integration tests.
 */
class ProcessorPendingSpec: StringSpec({

    for (testSet in sources()) {
        "native - $testSet".config(enabled = false) {
            val folder = tempdir()
            val reader = ResourceReader(ProcessorPendingSpec::class.java)

            val testFiles = TestFilesNative(folder, reader)
            val test = Test(testSet, testFiles)

            TestSetRunner(test, testSet)
                .run()
                .shouldBeTrue()
        }
    }

    for (testSet in sources()) {
        "jimfs - $testSet".config(enabled = false) {
            val fs = Jimfs.newFileSystem (Configuration.unix ())
            val reader = ResourceReader(ProcessorPendingSpec::class.java)

            val testFiles = TestFilesJimfs(fs, reader)
            val test = Test(testSet, testFiles)

            TestSetRunner(test, testSet)
                .run()
                .shouldBeTrue()
        }
    }
})

private fun sources(): Collection<TestSet> {
    return listOf(
        testSet("annotation-mapping-class", INTERNAL, API_30, model = "model", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("annotation-mapping-class", SWAGGER, API_30, model = "model", outputs = "outputs.yaml", expected = "outputs"),
        testSet("annotation-mapping-class", OPENAPI4J, API_30, model = "model", outputs = "outputs.yaml", expected = "outputs"),
        testSet("server-url", INTERNAL, API_30, model = "model", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("javadoc", INTERNAL, API_30, model = "model", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("javadoc", INTERNAL, API_30, model = "record", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("map-many", INTERNAL, API_31, model = "record", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("map-many", INTERNAL, API_31, model = "default", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("map-many", INTERNAL, API_30, model = "record", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("map-many", INTERNAL, API_30, model = "default", outputs = "outputs.yaml", expected = "outputs"),
    )
}
