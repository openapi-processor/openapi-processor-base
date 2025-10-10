/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.test.TestSet

@Deprecated(message = "use other overload")
//@Suppress("SameParameterValue")
fun testSet(
    name: String,
    parser: ParserType,
    openapi: String = "openapi.yaml",
    model: String = "default",
    inputs: String = "inputs.yaml",
    outputs: String = "generated.yaml",
    expected: String = "generated"
): TestSet {
    val testSet = TestSet()
    testSet.name = name
    testSet.processor = TestProcessor()
    testSet.parser = parser.name
    testSet.modelType = model
    testSet.openapi = openapi
    testSet.inputs = inputs
    testSet.outputs = outputs
    testSet.expected = expected
    return testSet
}

//@Suppress("SameParameterValue")
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
