/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.test.*


val ALL_3x: List<TestParams2> = join(
    emptyList(),
    tests("annotation-mapping-class"),
    tests("bean-validation"),
    tests("bean-validation-allof-required"),
    tests("bean-validation-iterable"),
    tests("bean-validation-jakarta"),
    testX("bean-validation-list-item-import"),
    testX("bean-validation-mapping-supported"),
    testX("bean-validation-requestbody"),
    testX("bean-validation-requestbody-mapping"),
    tests("components-requestbodies"),
    tests("deprecated"),
    testX("endpoint-exclude"),
    testX("endpoint-http-mapping"), // framework specific
    tests("extension-mapping"),
    tests("format-eclipse"),
    tests("generated"),
    tests("javadoc"),
    tests("javadoc-with-mapping"),
    tests("json-input", apis = listOf("openapi32.json", "openapi31.json", "openapi30.json")),
    tests("keyword-identifier"),
    testX("map-from-additional-properties"),
    tests("map-from-additional-properties-with-package-name"),
    tests("map-many"),
    tests("map-to-primitive-data-types"),
    testX("method-operation-id"),
    tests("model-name-suffix"),
    tests("model-name-suffix-with-package-name"),
    testX("object-empty"),
    tests("object-nullable-properties"),
    tests("object-read-write-properties"),
    tests("object-without-properties"),
    tests(name = "packages", apis = listOf("api/$API_32", "api/$API_31", "api/$API_30")),
    testX("params-additional"),
    testX("params-additional-global"),
    tests("params-complex-data-types"), // framework specific
    testX("params-endpoint"),
    testX("params-enum"),
    testX("params-enum-string"),
    testX("params-path-simple-data-types"), // framework specific
    tests("params-request-body"), // framework specific
    testX("params-request-body-multipart-form-data"), // framework specific
    testX("params-simple-data-types"), // framework specific
    testX("params-unnecessary"),
    tests("ref-array-items-nested"),
    tests("ref-chain-spring-124.1"),
    tests("ref-chain-spring-124.2"),
    tests("ref-into-another-file"),
    tests("ref-into-another-file-path"),
    tests("ref-is-relative-to-current-file"),
    tests("ref-loop"),
    tests("ref-loop-array"),
    testX("ref-parameter"),
    testX("ref-parameter-with-primitive-mapping"),
    tests("ref-response"),
    tests("ref-to-escaped-path-name"),
    testX("response-array-data-type-mapping"),
    tests("response-complex-data-types"),
    tests("response-content-multiple-no-content"),
    tests("response-content-multiple-style-all"),
    tests("response-content-multiple-style-success"),
    testX("response-content-single"),
    tests("response-multi-mapping-with-array-type-mapping"),
    testX("response-reactive-mapping"),
    testX("response-reactive-result-mapping"),
    tests("response-ref-class-name"),
    testX("response-result-mapping"),
    tests("response-result-multiple"),
    tests("response-result-multiple-object"),
    testX("response-result-reactive-mapping"),
    testX("response-simple-data-types"),
    tests("response-single-multi-mapping"),
    testX("response-status"),
    tests("schema-composed"),
    tests("schema-composed-allof"),
    tests("schema-composed-allof-notype"),
    tests("schema-composed-allof-properties"),
    tests("schema-composed-allof-ref-sibling"),
    tests("schema-composed-nested"),
    tests("schema-composed-oneof-interface"),
    //tests("schema-duplicate-by-refs"), // not supported
    tests("schema-mapping"),
    tests("schema-unreferenced"),
    tests("server-url"),
    tests("swagger-parsing-error")
).sortedWith(TestParamComparator)

val EXCLUDE_OPENAPI4J = setOf(
    // the parser assumes that "type" must be string if a non-standard format is used
    "schema-mapping",
    // can't get uri of a document
    "packages",
    // complains about incompatible formats for integer
    "bean-validation-mapping-supported"
)

val EXCLUDE_SWAGGER = setOf(
    // the parser is strict about $ref elements and does ignore a description
    "javadoc",
    "format-eclipse",
    // can't get uri of a document
    "packages"
)

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