/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.test.*


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
    val compile30 = ALL_30.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val compile31 = ALL_31.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val compile30r = ALL_30.filter { it.modelTypes.contains(ModelTypes.RECORD) }.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "record", outputs = it.outputs, expected = it.expected)
    }

    val compile31r = ALL_31.filter { it.modelTypes.contains(ModelTypes.RECORD) }.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "record", outputs = it.outputs, expected = it.expected)
    }

    return compile30 + compile31 + compile30r + compile31r
}
