/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

data class TestParams(
    val name: String,
    val openapi: String,
    val outputs: String = "generated.yaml",
    val expected: String = "generated",
    val records: Boolean = false
)

fun testParams30(
    name: String,
    openapi: String = API_30,
    outputs: String = "outputs.yaml",
    expected: String = "outputs"
): TestParams {
    return TestParams(name, openapi, outputs, expected, true)
}

fun testParams31(
    name: String,
    openapi: String = API_31,
    outputs: String = "outputs.yaml",
    expected: String = "outputs"
): TestParams {
    return TestParams(name, openapi, outputs, expected, true)
}

val ALL_30: List<TestParams> = listOf(
    testParams30("annotation-mapping-class"),
    testParams30("bean-validation"),
    testParams30("bean-validation-allof-required"),
    testParams30("bean-validation-iterable"),
    testParams30("bean-validation-jakarta"),
    TestParams("bean-validation-list-item-import", API_30),
    TestParams("bean-validation-requestbody", API_30),
    TestParams("bean-validation-requestbody-mapping", API_30),
    TestParams("components-requestbodies", API_30),
    TestParams("deprecated", API_30),
    TestParams("endpoint-exclude", API_30),
    TestParams("endpoint-http-mapping", API_30), // framework specific
    TestParams("generated", API_30),
    TestParams("javadoc", API_30),
    TestParams("javadoc-with-mapping", API_30),
    TestParams("keyword-identifier", API_30, outputs = "outputs.yaml", expected = "outputs"),
    TestParams("map-from-additional-properties", API_30),
    TestParams("map-from-additional-properties-with-package-name", API_30),
    TestParams("method-operation-id", API_30),
    TestParams("model-name-suffix", API_30),
    TestParams("model-name-suffix-with-package-name", API_30),
    TestParams("object-empty", API_30),
    TestParams("object-nullable-properties", API_30),
    TestParams("object-read-write-properties", API_30),
    TestParams("object-without-properties", API_30),
    TestParams("params-additional", API_30),
    TestParams("params-additional-global", API_30),
    TestParams("params-complex-data-types", API_30), // framework specific
    TestParams("params-endpoint", API_30),
    TestParams("params-enum", API_30),
    TestParams("params-path-simple-data-types", API_30), // framework specific
    TestParams("params-request-body", API_30), // framework specific
    TestParams("params-request-body-multipart-form-data", API_30), // framework specific
    TestParams("params-simple-data-types", API_30), // framework specific
    TestParams("ref-array-items-nested", API_30),
    TestParams("ref-chain-spring-124.1", API_30),
    TestParams("ref-chain-spring-124.2", API_30),
    TestParams("ref-into-another-file", API_30),
    TestParams("ref-into-another-file-path", API_30),
    TestParams("ref-is-relative-to-current-file", API_30),
    TestParams("ref-loop", API_30),
    TestParams("ref-loop-array", API_30),
    TestParams("ref-parameter", API_30),
    TestParams("ref-parameter-with-primitive-mapping", API_30),
    TestParams("ref-to-escaped-path-name", API_30),
    TestParams("response-array-data-type-mapping", API_30),
    TestParams("response-complex-data-types", API_30),
    TestParams("response-content-multiple-no-content", API_30),
    TestParams("response-content-multiple-style-all", API_30),
    TestParams("response-content-multiple-style-success", API_30),
    TestParams("response-content-single", API_30),
    TestParams("response-multi-mapping-with-array-type-mapping", API_30),
    TestParams("response-result-mapping", API_30),
    TestParams("response-simple-data-types", API_30),
    TestParams("response-single-multi-mapping", API_30),
    TestParams("schema-composed", API_30),
    TestParams("schema-composed-allof", API_30),
    TestParams("schema-composed-allof-notype", API_30),
    TestParams("schema-composed-allof-properties", API_30),
    TestParams("schema-composed-allof-ref-sibling", API_30),
    TestParams("schema-composed-nested", API_30),
    TestParams("schema-composed-oneof-interface", API_30)
)

val ALL_31: List<TestParams> = listOf(
    testParams31("annotation-mapping-class"),
    testParams31("bean-validation"),
    testParams31("bean-validation-allof-required"),
    testParams31("bean-validation-iterable"),
    testParams31("bean-validation-jakarta"),
    TestParams("bean-validation-list-item-import", API_31),
    TestParams("bean-validation-requestbody", API_31),
    TestParams("bean-validation-requestbody-mapping", API_31),
    TestParams("components-requestbodies", API_31),
    TestParams("deprecated", API_31),
    TestParams("endpoint-exclude", API_31),
    TestParams("endpoint-http-mapping", API_31), // framework specific
    TestParams("generated", API_31),
    TestParams("javadoc", API_31),
    TestParams("javadoc-with-mapping", API_31),
    TestParams("keyword-identifier", API_31, outputs = "outputs.yaml", expected = "outputs"),
    TestParams("map-from-additional-properties", API_31),
    TestParams("map-from-additional-properties-with-package-name", API_31),
    TestParams("method-operation-id", API_31),
    TestParams("model-name-suffix", API_31),
    TestParams("model-name-suffix-with-package-name", API_31),
    TestParams("object-empty", API_31),
    TestParams("object-nullable-properties", API_31),
    TestParams("object-read-write-properties", API_31),
    TestParams("object-without-properties", API_31),
    TestParams("params-additional", API_31),
    TestParams("params-additional-global", API_31),
    TestParams("params-complex-data-types", API_31), // framework specific
    TestParams("params-endpoint", API_31),
    TestParams("params-enum", API_31),
    TestParams("params-path-simple-data-types", API_31), // framework specific
    TestParams("params-request-body", API_31), // framework specific
    TestParams("params-request-body-multipart-form-data", API_31), // framework specific
    TestParams("params-simple-data-types", API_31), // framework specific
    TestParams("ref-array-items-nested", API_31),
    TestParams("ref-chain-spring-124.1", API_31),
    TestParams("ref-chain-spring-124.2", API_31),
    TestParams("ref-into-another-file", API_31),
    TestParams("ref-into-another-file-path", API_31),
    TestParams("ref-is-relative-to-current-file", API_31),
    TestParams("ref-loop", API_31),
    TestParams("ref-loop-array", API_31),
    TestParams("ref-parameter", API_31),
    TestParams("ref-parameter-with-primitive-mapping", API_31),
    TestParams("ref-to-escaped-path-name", API_31),
    TestParams("response-array-data-type-mapping", API_31),
    TestParams("response-complex-data-types", API_31),
    TestParams("response-content-multiple-no-content", API_31),
    TestParams("response-content-multiple-style-all", API_31),
    TestParams("response-content-multiple-style-success", API_31),
    TestParams("response-content-single", API_31),
    TestParams("response-multi-mapping-with-array-type-mapping", API_31),
    TestParams("response-result-mapping", API_31),
    TestParams("response-simple-data-types", API_31),
    TestParams("response-single-multi-mapping", API_31),
    TestParams("schema-composed", API_31),
    TestParams("schema-composed-allof", API_31),
    TestParams("schema-composed-allof-notype", API_31),
    TestParams("schema-composed-allof-properties", API_31),
    TestParams("schema-composed-allof-ref-sibling", API_31),
    TestParams("schema-composed-nested", API_31),
    TestParams("schema-composed-oneof-interface", API_31)
)
