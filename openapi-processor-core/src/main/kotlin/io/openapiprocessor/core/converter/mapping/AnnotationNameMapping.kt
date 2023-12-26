/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Mapping of a (parameter) name to a java annotation, i.e. `name @ annotation`.
 */
interface AnnotationNameMapping {
    /**
     * The property/parameter name of this mapping. Must match 1:1 with what is written in the api.
     */
    val name: String

    /**
     * additional annotation of the type.
     */
    val annotation: Annotation
}
