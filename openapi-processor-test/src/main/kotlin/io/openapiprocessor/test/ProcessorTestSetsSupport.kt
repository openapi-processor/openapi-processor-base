/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

const val API_30 = "openapi30.yaml"
const val API_31 = "openapi31.yaml"

enum class ModelTypes {DEFAULT, RECORD}

data class TestParams(
    val name: String,
    val openapi: String,
    val outputs: String = "generated.yaml",
    val expected: String = "generated",
    val modelTypes: List<ModelTypes> = listOf(ModelTypes.DEFAULT)
)

fun test30_D_(
    name: String,
    openapi: String = API_30,
    outputs: String = "outputs.yaml",
    expected: String = "outputs",
    modelTypes: List<ModelTypes> = listOf(ModelTypes.DEFAULT)
): TestParams {
    return TestParams(name, openapi, outputs, expected, modelTypes)
}

fun test30_DR(
    name: String,
    openapi: String = API_30,
    outputs: String = "outputs.yaml",
    expected: String = "outputs",
    modelTypes: List<ModelTypes> = listOf(ModelTypes.DEFAULT, ModelTypes.RECORD)
): TestParams {
    return TestParams(name, openapi, outputs, expected, modelTypes)
}

fun test31_D_(
    name: String,
    openapi: String = API_31,
    outputs: String = "outputs.yaml",
    expected: String = "outputs",
    modelTypes: List<ModelTypes> = listOf(ModelTypes.DEFAULT)
): TestParams {
    return TestParams(name, openapi, outputs, expected, modelTypes)
}

fun test31_DR(
    name: String,
    openapi: String = API_31,
    outputs: String = "outputs.yaml",
    expected: String = "outputs",
    modelTypes: List<ModelTypes> = listOf(ModelTypes.DEFAULT, ModelTypes.RECORD)
): TestParams {
    return TestParams(name, openapi, outputs, expected, modelTypes)
}
