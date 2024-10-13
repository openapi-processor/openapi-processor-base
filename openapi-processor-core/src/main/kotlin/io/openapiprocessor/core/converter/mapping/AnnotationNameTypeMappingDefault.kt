/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

open class AnnotationNameTypeMappingDefault(
    /**
     * The parameter name of this mapping. Must match 1:1 with what is written in the api.
     */
    override val name: String,

    /**
     * additional annotation of the type.
     */
    override val annotation: Annotation

): Mapping, AnnotationNameMapping {

    override fun toString(): String {
        return "name: $name @ $annotation"
    }
}
