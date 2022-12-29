/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model.datatypes

import io.openapiprocessor.core.model.Documentation

/**
 * OpenAPI type 'number' with format 'float' maps to java Float.
 */
class FloatDataType(
    private val typeFormat: String = "number:float",
    override val constraints: DataTypeConstraints? = null,
    override val deprecated: Boolean = false,
    override val documentation: Documentation? = null
): DataType, SimpleDataType {

    override fun getName(): String {
        return typeFormat
    }

    override fun getTypeName(): String {
        return "Float"
    }

    override fun getPackageName(): String {
        return "java.lang"
    }

    override fun getImports(): Set<String> {
        return setOf("${getPackageName()}.${getTypeName()}")
    }

}
