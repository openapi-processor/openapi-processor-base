/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core

import io.openapiprocessor.test.*

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
    test30_D_("response-result-mapping"),
    test30_DR("response-result-multiple"),
    test30_D_("response-result-reactive-mapping"),
    test30_D_("response-simple-data-types"),
    test30_DR("response-single-multi-mapping"),
    test30_DR("schema-composed"),
    test30_DR("schema-composed-allof"),
    test30_DR("schema-composed-allof-notype"),
    test30_DR("schema-composed-allof-properties"),
    test30_DR("schema-composed-allof-ref-sibling"),
    test30_DR("schema-composed-nested"),
    test30_DR("schema-composed-oneof-interface"),
    //test30_DR("schema-duplicate-by-refs"), // not supported
    test30_DR("schema-mapping"),
    test30_DR("server-url"),
    test30_DR("swagger-parsing-error")
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
    test31_D_("response-result-mapping"),
    test31_DR("response-result-multiple"),
    test31_D_("response-result-reactive-mapping"),
    test31_D_("response-simple-data-types"),
    test31_DR("response-single-multi-mapping"),
    test31_DR("schema-composed"),
    test31_DR("schema-composed-allof"),
    test31_DR("schema-composed-allof-notype"),
    test31_DR("schema-composed-allof-properties"),
    test31_DR("schema-composed-allof-ref-sibling"),
    test31_DR("schema-composed-nested"),
    test31_DR("schema-composed-oneof-interface"),
    //test31_DR("schema-duplicate-by-refs"), // not supported
    test31_DR("schema-mapping"),
    test31_DR("server-url"),
    test31_DR("swagger-parsing-error")
)

val EXCLUDE_OPENAPI4J = setOf(
    // the parser assumes that "type" must be string if a non-standard format is used
    "schema-mapping"
)

val EXCLUDE_SWAGGER_30 = setOf(
    // the parser is strict about $ref elements and does ignore a description
    "javadoc",
    "format-eclipse"
)
