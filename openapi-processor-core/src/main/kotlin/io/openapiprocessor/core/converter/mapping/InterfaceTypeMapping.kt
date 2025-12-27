/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Used to map OpenAPI schemas to implemented java interface types.
 */
class InterfaceTypeMapping(
    /**
     * The OpenAPI schema type that should implement the {@link #targetTypeName} java type.
     */
    val sourceTypeName: String,

    /**
     * The fully qualified java type interface name to implement.
     */
    val targetTypeName: String,

    /**
     * The fully qualified java type names of all generic parameters of {@link #targetTypeName}.
     */
    val genericTypes: List<TargetType> = emptyList()
): Mapping, TargetTypeMapping {

    override fun getTargetType(): TargetType {
        return TargetType(targetTypeName, genericTypes)
    }
}
