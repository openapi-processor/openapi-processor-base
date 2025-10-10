/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.test.ResourceReader
import io.openapiprocessor.test.TestFilesNative
import io.openapiprocessor.test.TestSet
import io.openapiprocessor.test.TestSetCompiler


class CompileExpectedSpec: StringSpec({

    for (testSet in sources()) {
        "compile - $testSet".config(enabled = true) {
            val folder = tempdir()
            val reader = ResourceReader(CompileExpectedSpec::class.java)

            val testFiles = TestFilesNative(folder, reader)

            TestSetCompiler(testSet, testFiles)
                .run(setOf(
                    "src/testInt/resources/compile/Generated.java",
                    "src/testInt/resources/compile/Mapping.java",
                    "src/testInt/resources/compile/Parameter.java",
                    "src/testInt/resources/compile/Status.java"
                ))
                .shouldBeTrue()
        }
    }
})

private fun sources(): Collection<TestSet> {
    return buildTestSets()
}
