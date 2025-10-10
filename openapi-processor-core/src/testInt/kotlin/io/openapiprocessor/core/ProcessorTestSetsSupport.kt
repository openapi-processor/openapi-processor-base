/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.test.TestSet

fun testSet(
    name: String,
    parser: String = "INTERNAL",
    openapi: String = "openapi.yaml",
    model: String = "default",
    inputs: String = "inputs.yaml",
    outputs: String = "outputs.yaml",
    expected: String = "outputs"
): TestSet {
    val testSet = TestSet()
    testSet.name = name
    testSet.processor = TestProcessor()
    testSet.parser = parser
    testSet.modelType = model
    testSet.openapi = openapi
    testSet.inputs = inputs
    testSet.outputs = outputs
    testSet.expected = expected
    return testSet
}

fun buildTestSets(): List<TestSet> {
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
