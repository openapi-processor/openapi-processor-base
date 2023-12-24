/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Mapping of an OpenAPI type to a java annotation, i.e. `OpenAPI type @ annotation`.
 */
interface AnnotationTypeMapping {
    /**
     * the OpenAPI schema type that should be annotated with [annotation].
     */
    val sourceTypeName: String

    /**
     * The OpenAPI format of [sourceTypeName], if any.
     */
    val sourceTypeFormat: String?

    /**
     * additional annotation of the type.
     */
    val annotation: Annotation
}
