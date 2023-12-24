/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

open class AnnotationTypeMappingDefault(
    /**
     * the OpenAPI schema type that should be annotated with [annotation].
     */
    override val sourceTypeName: String,

    /**
     * The OpenAPI format of [sourceTypeName], if any.
     */
    override val sourceTypeFormat: String? = null,

    /**
     * additional annotation of the type.
     */
    override val annotation: Annotation

): Mapping, AnnotationTypeMapping
