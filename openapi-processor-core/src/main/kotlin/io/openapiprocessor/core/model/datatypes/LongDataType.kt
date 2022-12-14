/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * OpenAPI type 'integer' with format 'int64' maps to java Long.
 */
class LongDataType(
    private val typeFormat: String = "integer:int64",
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false,
    override val documentation: Documentation? = null
): DataType, SimpleDataType {

    override fun getName(): String {
        return typeFormat
    }

    override fun getTypeName(): String {
        return "Long"
    }

    override fun getPackageName(): String {
        return "java.lang"
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.${getTypeName()}")
    }

}
