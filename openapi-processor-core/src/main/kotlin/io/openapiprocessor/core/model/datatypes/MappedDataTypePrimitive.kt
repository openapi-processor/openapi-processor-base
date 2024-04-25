/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * OpenAPI schema mapped to a primitive java type.
 */
open class MappedDataTypePrimitive(
    private val type: String,
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false,
    override val sourceDataType: DataType? = null
): DataType, SourceDataType {

    override fun getName(): String {
        return type
    }

    override fun getSourceName(): String {
        return sourceDataType?.getName() ?: super.getSourceName()
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
