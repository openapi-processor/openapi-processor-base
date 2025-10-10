/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.test.*


private fun join(vararg collections: Collection<TestParams2>): List<TestParams2> {
    return collections.flatMap { it }
}

private val testParamComparator = Comparator<TestParams2> { l, r ->
    val parser = l.parser.compareTo(r.parser)
    if (parser != 0) {
        return@Comparator parser
    }

    val name = l.name.compareTo(r.name)
    if (name != 0) {
        return@Comparator name
    }

    val openapi = l.openapi.compareTo(r.openapi)
    if (openapi != 0) {
        return@Comparator openapi
    }

    return@Comparator 0
}

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
).sortedWith(testParamComparator)

@Deprecated(message = "merged into ALL_3x")
val ALL_30: List<TestParams> = listOf(
    test30_DR("annotation-mapping-class"),
    test30_DR("bean-validation"),
    test30_DR("bean-validation-allof-required"),
    test30_DR("bean-validation-iterable"),
    test30_DR("bean-validation-jakarta"),
    test30_D_("bean-validation-list-item-import"),
    test30_D_("bean-validation-mapping-supported"),
    test30_D_("bean-validation-requestbody"),
    test30_D_("bean-validation-requestbody-mapping"),
    test30_DR("components-requestbodies"),
    test30_DR("deprecated"),
    test30_D_("endpoint-exclude"),
    test30_D_("endpoint-http-mapping"), // framework specific
    test30_DR("extension-mapping"),
    test30_DR("format-eclipse"),
    test30_DR("generated"),
    test30_DR("javadoc"),
    test30_DR("javadoc-with-mapping"),
    test30_DR("json-input", "openapi30.json"),
    test30_DR("keyword-identifier"),
    test30_D_("map-from-additional-properties"),
    test30_DR("map-from-additional-properties-with-package-name"),
    test30_DR("map-many"),
    test30_DR("map-to-primitive-data-types"),
    test30_D_("method-operation-id"),
    test30_DR("model-name-suffix"),
    test30_DR("model-name-suffix-with-package-name"),
    test30_D_("object-empty"),
    test30_DR("object-nullable-properties"),
    test30_DR("object-read-write-properties"),
    test30_DR("object-without-properties"),
    test30_DR(name = "packages", openapi = "api/$API_30"),
    test30_D_("params-additional"),
    test30_D_("params-additional-global"),
    test30_DR("params-complex-data-types"), // framework specific
    test30_D_("params-endpoint"),
    test30_D_("params-enum"),
    test30_D_("params-enum-string"),
    test30_D_("params-path-simple-data-types"), // framework specific
    test30_DR("params-request-body"), // framework specific
    test30_D_("params-request-body-multipart-form-data"), // framework specific
    test30_D_("params-simple-data-types"), // framework specific
    test30_D_("params-unnecessary"),
    test30_DR("ref-array-items-nested"),
    test30_DR("ref-chain-spring-124.1"),
    test30_DR("ref-chain-spring-124.2"),
    test30_DR("ref-into-another-file"),
    test30_DR("ref-into-another-file-path"),
    test30_DR("ref-is-relative-to-current-file"),
    test30_DR("ref-loop"),
    test30_DR("ref-loop-array"),
    test30_D_("ref-parameter"),
    test30_D_("ref-parameter-with-primitive-mapping"),
    test30_DR("ref-response"),
    test30_DR("ref-to-escaped-path-name"),
    test30_D_("response-array-data-type-mapping"),
    test30_DR("response-complex-data-types"),
    test30_DR("response-content-multiple-no-content"),
    test30_DR("response-content-multiple-style-all"),
    test30_DR("response-content-multiple-style-success"),
    test30_D_("response-content-single"),
    test30_DR("response-multi-mapping-with-array-type-mapping"),
    test30_D_("response-reactive-mapping"),
    test30_D_("response-reactive-result-mapping"),
    test30_DR("response-ref-class-name"),
    test30_D_("response-result-mapping"),
    test30_DR("response-result-multiple"),
    test30_DR("response-result-multiple-object"),
    test30_D_("response-result-reactive-mapping"),
    test30_D_("response-simple-data-types"),
    test30_DR("response-single-multi-mapping"),
    test30_D_("response-status"),
    test30_DR("schema-composed"),
    test30_DR("schema-composed-allof"),
    test30_DR("schema-composed-allof-notype"),
    test30_DR("schema-composed-allof-properties"),
    test30_DR("schema-composed-allof-ref-sibling"),
    test30_DR("schema-composed-nested"),
    test30_DR("schema-composed-oneof-interface"),
    //test30_DR("schema-duplicate-by-refs"), // not supported
    test30_DR("schema-mapping"),
    test30_DR("schema-unreferenced"),
    test30_DR("server-url"),
    test30_DR("swagger-parsing-error")
)

@Deprecated(message = "merged into ALL_3x")
val ALL_31: List<TestParams> = listOf(
    test31_DR("annotation-mapping-class"),
    test31_DR("bean-validation"),
    test31_DR("bean-validation-allof-required"),
    test31_DR("bean-validation-iterable"),
    test31_DR("bean-validation-jakarta"),
    test31_D_("bean-validation-list-item-import"),
    test31_D_("bean-validation-mapping-supported"),
    test31_D_("bean-validation-requestbody"),
    test31_D_("bean-validation-requestbody-mapping"),
    test31_DR("components-requestbodies"),
    test31_DR("extension-mapping"),
    test31_DR("format-eclipse"),
    test31_DR("deprecated"),
    test31_D_("endpoint-exclude"),
    test31_D_("endpoint-http-mapping"), // framework specific
    test31_DR("generated"),
    test31_DR("javadoc"),
    test31_DR("javadoc-with-mapping"),
    test31_DR("json-input", "openapi31.json"),
    test31_DR("keyword-identifier"),
    test31_D_("map-from-additional-properties"),
    test31_DR("map-from-additional-properties-with-package-name"),
    test31_DR("map-many"),
    test31_DR("map-to-primitive-data-types"),
    test31_D_("method-operation-id"),
    test31_DR("model-name-suffix"),
    test31_DR("model-name-suffix-with-package-name"),
    test31_D_("object-empty"),
    test31_DR("object-nullable-properties"),
    test31_DR("object-read-write-properties"),
    test31_DR("object-without-properties"),
    test31_DR(name = "packages", openapi = "api/$API_31"),
    test31_D_("params-additional"),
    test31_D_("params-additional-global"),
    test31_DR("params-complex-data-types"), // framework specific
    test31_D_("params-endpoint"),
    test31_D_("params-enum"),
    test31_D_("params-enum-string"),
    test31_D_("params-path-simple-data-types"), // framework specific
    test31_DR("params-request-body"), // framework specific
    test31_D_("params-request-body-multipart-form-data"), // framework specific
    test31_D_("params-simple-data-types"), // framework specific
    test31_D_("params-unnecessary"),
    test31_DR("ref-array-items-nested"),
    test31_DR("ref-chain-spring-124.1"),
    test31_DR("ref-chain-spring-124.2"),
    test31_DR("ref-into-another-file"),
    test31_DR("ref-into-another-file-path"),
    test31_DR("ref-is-relative-to-current-file"),
    test31_DR("ref-loop"),
    test31_DR("ref-loop-array"),
    test31_D_("ref-parameter"),
    test31_D_("ref-parameter-with-primitive-mapping"),
    test31_DR("ref-response"),
    test31_DR("ref-to-escaped-path-name"),
    test31_D_("response-array-data-type-mapping"),
    test31_DR("response-complex-data-types"),
    test31_DR("response-content-multiple-no-content"),
    test31_DR("response-content-multiple-style-all"),
    test31_DR("response-content-multiple-style-success"),
    test31_D_("response-content-single"),
    test31_DR("response-multi-mapping-with-array-type-mapping"),
    test31_D_("response-reactive-mapping"),
    test31_D_("response-reactive-result-mapping"),
    test31_DR("response-ref-class-name"),
    test31_D_("response-result-mapping"),
    test31_DR("response-result-multiple"),
    test31_DR("response-result-multiple-object"),
    test31_D_("response-result-reactive-mapping"),
    test31_D_("response-simple-data-types"),
    test31_DR("response-single-multi-mapping"),
    test31_D_("response-status"),
    test31_DR("schema-composed"),
    test31_DR("schema-composed-allof"),
    test31_DR("schema-composed-allof-notype"),
    test31_DR("schema-composed-allof-properties"),
    test31_DR("schema-composed-allof-ref-sibling"),
    test31_DR("schema-composed-nested"),
    test31_DR("schema-composed-oneof-interface"),
    //test31_DR("schema-duplicate-by-refs"), // not supported
    test31_DR("schema-mapping"),
    test31_DR("schema-unreferenced"),
    test31_DR("server-url"),
    test31_DR("swagger-parsing-error")
)

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
