/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * OpenAPI schema type/collection mapped to a java primitive array type.
 */
open class MappedCollectionDataTypePrimitive(
    private val name: String,
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false,
    override val sourceDataType: DataType? = null
): DataType, CollectionDataType, MappedSourceDataType {

    override fun getName(): String {
        return "${name}[]"
    }

    override fun getSourceName(): String {
        return sourceDataType?.getName() ?: super.getSourceName()
    }

    override val item: DataType
        get() = this

    override fun getTypeName(annotations: Set<String>, itemAnnotations: Set<String>): String {
        val sb = StringBuilder()

        if (annotations.isNotEmpty()) {
            sb.append(annotations.joinToString(" ", "", " "))
        }
        sb.append(getTypeName())

        return sb.toString()
    }

    override fun getPackageName(): String {
        return ""
    }

    override fun getImports(): Set<String> {
        return emptySet()
    }

    override val documentation: Documentation?
        get() = sourceDataType?.documentation
}
