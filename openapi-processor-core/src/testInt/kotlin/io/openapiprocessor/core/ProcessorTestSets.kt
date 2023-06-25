/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

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

val ALL_30: List<TestParams> = listOf(
    test30_DR("annotation-mapping-class"),
    test30_DR("bean-validation"),
    test30_DR("bean-validation-allof-required"),
    test30_DR("bean-validation-iterable"),
    test30_DR("bean-validation-jakarta"),
    test30_D_("bean-validation-list-item-import"),
    test30_D_("bean-validation-requestbody"),
    test30_D_("bean-validation-requestbody-mapping"),
    test30_DR("components-requestbodies"),
    test30_DR("deprecated"),
    test30_D_("endpoint-exclude"),
    test30_D_("endpoint-http-mapping"), // framework specific
    test30_DR("generated"),
    test30_DR("javadoc"),
    test30_DR("javadoc-with-mapping"),
    test30_DR("keyword-identifier"),
    test30_D_("map-from-additional-properties"),
    test30_DR("map-from-additional-properties-with-package-name"),
    test30_D_("method-operation-id"),
    test30_DR("model-name-suffix"),
    test30_DR("model-name-suffix-with-package-name"),
    test30_D_("object-empty"),
    test30_DR("object-nullable-properties"),
    test30_DR("object-read-write-properties"),
    test30_DR("object-without-properties"),
    test30_D_("params-additional"),
    test30_D_("params-additional-global"),
    test30_DR("params-complex-data-types"), // framework specific
    test30_D_("params-endpoint"),
    test30_D_("params-enum"),

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
    test31_DR("annotation-mapping-class"),
    test31_DR("bean-validation"),
    test31_DR("bean-validation-allof-required"),
    test31_DR("bean-validation-iterable"),
    test31_DR("bean-validation-jakarta"),
    test31_D_("bean-validation-list-item-import"),
    test31_D_("bean-validation-requestbody"),
    test31_D_("bean-validation-requestbody-mapping"),
    test31_DR("components-requestbodies"),
    test31_DR("deprecated"),
    test31_D_("endpoint-exclude"),
    test31_D_("endpoint-http-mapping"), // framework specific
    test31_DR("generated"),
    test31_DR("javadoc"),
    test31_DR("javadoc-with-mapping"),
    test31_DR("keyword-identifier"),
    test31_D_("map-from-additional-properties"),
    test31_DR("map-from-additional-properties-with-package-name"),
    test31_D_("method-operation-id"),
    test31_DR("model-name-suffix"),
    test31_DR("model-name-suffix-with-package-name"),
    test31_D_("object-empty"),
    test31_DR("object-nullable-properties"),
    test31_DR("object-read-write-properties"),
    test31_DR("object-without-properties"),
    test31_D_("params-additional"),
    test31_D_("params-additional-global"),
    test31_DR("params-complex-data-types"), // framework specific
    test31_D_("params-endpoint"),
    test31_D_("params-enum"),

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
