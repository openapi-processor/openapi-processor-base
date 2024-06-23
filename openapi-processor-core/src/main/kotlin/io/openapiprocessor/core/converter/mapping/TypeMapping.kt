/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

/**
 * Used with [io.openapiprocessor.core.converter.ApiOptions.typeMappings] to map OpenAPI schemas
 * to java types.
 *
 * To override the type mapping of the OpenAPI `array` from a simple java array to another
 * collection type the [sourceTypeName] should be set to `array`.
 */
class  TypeMapping @JvmOverloads constructor(

    /**
     * The OpenAPI schema type that should be mapped to the {@link #targetTypeName} java type.
     */
    val sourceTypeName: String?,  // todo optional ??

    /**
     * The OpenAPI format of {@link #sourceTypeName} that should be mapped to the
     * {@link #targetTypeName} java type.
     */
    val sourceTypeFormat: String? = null,

    /**
     * The fully qualified java type name that will replace {@link #sourceTypeName}.
     */
    val targetTypeName: String,

    /**
     * The fully qualified java type names of all generic parameters to {@link #targetTypeName}.
     */
    val genericTypes: List<TargetType> = emptyList(),

    /**
     * is a primitive target type, i.e. byte, short int etc. ?
     */
    val primitive: Boolean = false,

    /**
     * is an array, i.e. type []?
     */
    val primitiveArray: Boolean = false

): Mapping, TargetTypeMapping {
    /**
     * Returns the target type of this type mapping.
     *
     * @return the target type
     */
    override fun getTargetType (): TargetType {
        return TargetType(targetTypeName, genericTypes)
    }

    override fun getChildMappings(): List<Mapping> {
        return listOf(this)
    }

    override fun toString(): String {
        return "${sourceType()} => ${targetType()}"
    }

    private fun sourceType(): String {
        return if (sourceTypeFormat == null) {
            sourceTypeName!!
        } else {
            "${sourceTypeName}:${sourceTypeFormat}"
        }
    }

    private fun targetType(): String {
        return "${targetTypeName}${targetTypeGenerics()}"
    }

    private fun targetTypeGenerics(): String {
        return if (genericTypes.isEmpty()) {
            ""
        } else {
            genericTypes.joinToString(",", "<", ">") { it.toString() }
        }
    }
}

fun List<Mapping>.toTypeMapping(): List<TypeMapping> {
    return map { it as TypeMapping }
}


