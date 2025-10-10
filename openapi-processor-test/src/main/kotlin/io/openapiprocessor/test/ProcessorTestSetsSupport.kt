/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-test
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test

const val API_30 = "openapi30.yaml"
const val API_31 = "openapi31.yaml"
const val API_32 = "openapi32.yaml"

val APIS_ALL = listOf(API_32, API_31, API_30)
val APIS_30 = listOf(API_30)

const val I = "INTERNAL"
const val S = "SWAGGER"
const val O = "OPENAPI4J"

val PA_ALL = listOf(I, S, O)
val PA_SO = listOf(S, O)
val PA_I = listOf(I)

const val D = "default"
const val R = "record"
val MT_ALL = listOf(D, R)
val MT_X = listOf(D)
val MT_D = listOf(D)
val MT_R = listOf(R)

@Deprecated("obsolete")
enum class ModelTypes {DEFAULT, RECORD}

@Deprecated("TestParams2")
data class TestParams(
    val name: String,
    val openapi: String,
    val outputs: String = "generated.yaml",
    val expected: String = "generated",
    val modelTypes: List<ModelTypes> = listOf(ModelTypes.DEFAULT)
)

data class TestParams2(
    val name: String,
    val openapi: String,
    val outputs: String = "outputs.yaml",
    val expected: String = "outputs",
    val modelType: String = D,
    val parser: String = I
)


fun tests(
    name: String,
    apis: List<String> = APIS_ALL,
    parsers: List<String> = PA_ALL,
    modelTypes: List<String> = MT_ALL,
    outputs: String = "outputs.yaml",
    expected: String = "outputs"
): Collection<TestParams2> {
    val params = mutableListOf<TestParams2>()

    apis.forEach { apiVersion ->
        modelTypes.forEach { modelType ->
            parsers.forEach { parser ->
                params.add(TestParams2(name,apiVersion, outputs, expected, modelType, parser))
            }
        }
    }

    return params
}

// no models, avoids duplicate tests
fun testX(name: String): Collection<TestParams2> {
    return tests(name, modelTypes = MT_X)
}




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
