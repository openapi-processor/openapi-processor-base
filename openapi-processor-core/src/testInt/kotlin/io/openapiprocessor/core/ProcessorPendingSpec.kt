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
                testSet.inputs, testSet.generated)

            TestSetRunner(testSet, support)
            .runOnNativeFileSystem(folder)
            .shouldBeTrue()
        }
    }

    for (testSet in sources()) {
        "jimfs - $testSet".config(enabled = false) {
            val support = FileSupport(
                ProcessorPendingSpec::class.java,
                testSet.inputs, testSet.generated)

            TestSetRunner(testSet, support)
            .runOnCustomFileSystem(Jimfs.newFileSystem (Configuration.unix ()))
            .shouldBeTrue()
        }
    }
})

private fun sources(): Collection<TestSet> {
    return listOf(
        testSet("annotation-mapping-class", INTERNAL, API_30, model = "record"),
        testSet("annotation-mapping-class", INTERNAL, API_30, model = "default"),
//        testSet("map-from-additional-properties-with-package-name", SWAGGER, API_31),
//        testSet("map-from-additional-properties-with-package-name", OPENAPI4J, API_30),
//        testSet("map-from-additional-properties-with-package-name", INTERNAL, API_30),
//        testSet("map-from-additional-properties-with-package-name", INTERNAL, API_31),
//        testSet("map-from-additional-properties-with-package-name", SWAGGER, API_30),
//        testSet("javadoc-with-mapping", INTERNAL, API_31),
//        testSet("params-additional-global", INTERNAL, API_30),
//        testSet("params-additional-global", INTERNAL, API_31)
    )
}
