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
import io.openapiprocessor.core.parser.ParserType.*
import io.openapiprocessor.test.API_30
import io.openapiprocessor.test.API_31
import io.openapiprocessor.test.FileSupport
import io.openapiprocessor.test.TestSet
import io.openapiprocessor.test.TestSetRunner

/**
 * helper to run selected integration tests.
 */
class ProcessorPendingSpec: StringSpec({

    for (testSet in sources()) {
        "native - $testSet".config(enabled = false) {
            val folder = tempdir()

            val support = FileSupport(
                ProcessorPendingSpec::class.java,
                testSet.inputs, testSet.outputs
            )

            TestSetRunner(testSet, support, ProcessorPendingSpec::class.java)
            .runOnNativeFileSystem(folder)
            .shouldBeTrue()
        }
    }

    for (testSet in sources()) {
        "jimfs - $testSet".config(enabled = false) {
            val support = FileSupport(
                ProcessorPendingSpec::class.java,
                testSet.inputs, testSet.outputs
            )

            TestSetRunner(testSet, support, ProcessorPendingSpec::class.java)
            .runOnCustomFileSystem(Jimfs.newFileSystem (Configuration.unix ()))
            .shouldBeTrue()
        }
    }
})

private fun sources(): Collection<TestSet> {
    return listOf(
//        testSet("javadoc", INTERNAL, API_30, model = "model", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("javadoc", INTERNAL, API_30, model = "record", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("map-many", INTERNAL, API_31, model = "record", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("map-many", INTERNAL, API_31, model = "default", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("map-many", INTERNAL, API_30, model = "record", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("map-many", INTERNAL, API_30, model = "default", outputs = "outputs.yaml", expected = "outputs"),
    )
}
