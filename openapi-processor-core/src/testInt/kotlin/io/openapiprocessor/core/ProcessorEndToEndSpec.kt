/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.parser.ParserType.*
import io.openapiprocessor.test.*

/**
 * run end to end integration test.
 */
class ProcessorEndToEndSpec: StringSpec({

    for (testSet in sources()) {
        "native - $testSet".config(enabled = true) {
            val folder = tempdir()
            val reader = ResourceReader(ProcessorPendingSpec::class.java)

            val testFiles = TestFilesNative(folder, reader)
            val test = Test(testSet, testFiles)

            TestSetRunner(test, testSet)
                .runOnNativeFileSystem()
                .shouldBeTrue()
        }
    }
})

private fun sources(): Collection<TestSet> {
    val openapi4j = ALL_30.filter { !EXCLUDE_OPENAPI4J.contains(it.name) }.map {
        testSet(it.name, OPENAPI4J, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val swagger30 = ALL_30.filter { !EXCLUDE_SWAGGER_30.contains(it.name) }.map {
        testSet(it.name, SWAGGER, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val swagger30r = ALL_30.filter { !EXCLUDE_SWAGGER_30.contains(it.name) && it.modelTypes.contains(ModelTypes.RECORD) }.map {
        testSet(it.name, SWAGGER, it.openapi, model = "record", outputs = it.outputs, expected = it.expected)
    }

//    val swagger31 = ALL_31.filter { !EXCLUDE_SWAGGER_31.contains(it.name) }.map {
//        testSet(it.name, SWAGGER, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
//    }
//
//    val swagger31r = ALL_31.filter { !EXCLUDE_SWAGGER_31.contains(it.name) }.map {
//        testSet(it.name, SWAGGER, it.openapi, model = "record", outputs = it.outputs, expected = it.expected)
//    }

    val openapi30 = ALL_30.map {
        testSet(it.name, INTERNAL, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val openapi31 = ALL_31.map {
        testSet(it.name, INTERNAL, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val openapi30r = ALL_30.filter { it.modelTypes.contains(ModelTypes.RECORD) }.map {
        testSet(it.name, INTERNAL, it.openapi, model = "record", outputs = it.outputs, expected = it.expected)
    }

    val openapi31r = ALL_31.filter { it.modelTypes.contains(ModelTypes.RECORD) }.map {
        testSet(it.name, INTERNAL, it.openapi, model = "record", outputs = it.outputs, expected = it.expected)
    }

    return join(
        openapi4j,
        swagger30,
        swagger30r,
//        swagger31,
//        swagger31r,
        openapi30,
        openapi30r,
        openapi31,
        openapi31r
    )
}

private fun join(vararg collections: Collection<TestSet>): List<TestSet> {
    return collections.flatMap { it }
}
