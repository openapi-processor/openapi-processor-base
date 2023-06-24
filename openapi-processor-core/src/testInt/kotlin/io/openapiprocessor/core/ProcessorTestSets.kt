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

fun test30(
    name: String,
    openapi: String = API_30,
    outputs: String = "outputs.yaml",
    expected: String = "outputs",
    models: Boolean = true
): TestParams {
    return TestParams(name, openapi, outputs, expected, models)
}

fun test31(
    name: String,
    openapi: String = API_31,
    outputs: String = "outputs.yaml",
    expected: String = "outputs",
    models: Boolean = true
): TestParams {
    return TestParams(name, openapi, outputs, expected, models)
}

val ALL_30: List<TestParams> = listOf(
    test30("annotation-mapping-class"),
    test30("bean-validation"),
    test30("bean-validation-allof-required"),
    test30("bean-validation-iterable"),
    test30("bean-validation-jakarta"),
    test30("bean-validation-list-item-import", models = false),
    test30("bean-validation-requestbody", models = false),
    test30("bean-validation-requestbody-mapping", models = false),
    test30("components-requestbodies"),
    test30("deprecated"),
    test30("endpoint-exclude", models = false),
    test30("endpoint-http-mapping"), // framework specific
    test30("generated"),
    test30("javadoc"),
    test30("javadoc-with-mapping"),
    test30("keyword-identifier"),
    test30("map-from-additional-properties", models = false),
    test30("map-from-additional-properties-with-package-name"),
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
    test31("annotation-mapping-class"),
    test31("bean-validation"),
    test31("bean-validation-allof-required"),
    test31("bean-validation-iterable"),
    test31("bean-validation-jakarta"),
    test31("bean-validation-list-item-import", models = false),
    test31("bean-validation-requestbody", models = false),
    test31("bean-validation-requestbody-mapping", models = false),
    test31("components-requestbodies"),
    test31("deprecated"),
    test31("endpoint-exclude", models = false),
    test31("endpoint-http-mapping"), // framework specific
    test31("generated"),
    test31("javadoc"),
    test31("javadoc-with-mapping"),
    test31("keyword-identifier"),
    test31("map-from-additional-properties", models = false),
    test31("map-from-additional-properties-with-package-name"),
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
