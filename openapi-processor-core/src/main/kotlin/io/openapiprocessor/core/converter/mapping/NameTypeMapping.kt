/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * parameter name type mapping
 */
class NameTypeMapping(

    /**
     * The parameter name of this mapping. Must match 1:1 with what is written in the api.
     */
    val parameterName: String,

    /**
     * Type mapping valid only for requests with parameter {@link #parameterName}.
     */
    val mapping: TypeMapping

): Mapping {

    override fun toString(): String {
        return "$parameterName => ${targetType()}"
    }

    private fun targetType(): String {
        return "${mapping.targetTypeName}${targetTypeGenerics()}"
    }

    private fun targetTypeGenerics(): String {
        return if (mapping.genericTypes.isEmpty()) {
            ""
        } else {
            mapping.genericTypes.joinToString(",", "<", ">") { it.toString() }
        }
    }
}
