/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.test.*

/**
 * run end-to-end integration test.
 */
class ProcessorEndToEndSpec: StringSpec({

    for (testSet in sources()) {
        "native - $testSet".config(enabled = true) {
            val folder = tempdir()
            val reader = ResourceReader(ProcessorPendingSpec::class.java)

            val testFiles = TestFilesNative(folder, reader)
            val test = Test(testSet, testFiles)

            TestSetRunner(test, testSet)
                .run()
                .shouldBeTrue()
        }
    }
})

private fun sources(): Collection<TestSet> {
    return ALL_3x
        .filter {
            when (it.parser) {
                "INTERNAL" -> {
                    true
                }
                "SWAGGER" if it.openapi == "openapi30.yaml" -> {
                    !EXCLUDE_SWAGGER.contains(it.name)
                }
                "OPENAPI4J" if it.openapi == "openapi30.yaml" -> {
                    !EXCLUDE_OPENAPI4J.contains(it.name)
                }
                else -> {
                    false
                }
            }
        }
        .map {
            testSet(it.name, it.parser, it.openapi, model = it.modelType, outputs = it.outputs, expected = it.expected)
        }
}
