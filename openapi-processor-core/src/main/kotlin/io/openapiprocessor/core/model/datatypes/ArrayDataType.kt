/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

/**
 * OpenAPI type 'array' maps to java [].
 */
class ArrayDataType(
    override val item: DataType,
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false
): DataType by item, CollectionDataType {

    override fun getName(): String {
        return "${item.getName()}[]"
    }

    override fun getTypeName(): String {
        return "${item.getTypeName()}[]"
    }

    override fun getTypeName(annotations: Set<String>, itemAnnotations: Set<String>): String {
        val sb = StringBuilder()

        if (annotations.isNotEmpty()) {
            sb.append(annotations.joinToString(" ", "", " "))
        }
        sb.append(getTypeName())

        return sb.toString()
    }

    override val referencedImports: Set<String>
        get() = item.referencedImports

}
