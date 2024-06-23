/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Mapping target result created from {@link TypeMapping}.
 */
class TargetType(

    /**
     * type name
     */
    val typeName: String,

    /**
     * generic parameters of typeName
     */
    val genericTypes: List<TargetType> = emptyList()
) {

    /**
     * Returns the class name without the package name.
     *
     * @return the class name
     */
    fun getName(): String {
        return typeName.substring(typeName.lastIndexOf('.') + 1)
    }

    /**
     * Returns the package name.
     *
     * @return the package name
     */
    fun getPkg(): String {
        val dot = typeName.lastIndexOf('.')
        if (dot == -1)
            return ""

        return typeName.substring(0, dot)
    }

    override fun toString(): String {
        return targetType()
    }

    private fun targetType(): String {
        return "${typeName}${targetTypeGenerics()}"
    }

    private fun targetTypeGenerics(): String {
        return if (genericTypes.isEmpty()) {
            ""
        } else {
            genericTypes.joinToString(",", "<", ">") { it.toString() }
        }
    }
}
