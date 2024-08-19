/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Used with [io.openapiprocessor.core.converter.ApiOptions.typeMappings] to map an OpenAPI response
 * to a plain java type or to a wrapper type of the plain type.
 */
class ResultTypeMapping(

    /**
     * The fully qualified java type name that will be used as the result type.
     */
    val targetTypeName: String,

    /**
     * The fully qualified java type names of all generic parameters of {@link #targetTypeName}.
     */
    val genericTypes: List<TargetType> = emptyList()

): Mapping, TargetTypeMapping {
    /**
     * Returns the target type of this type mapping.
     *
     * @return the target type
     */
    override fun getTargetType(): TargetType {
        return TargetType(targetTypeName, genericTypes)
    }
}
