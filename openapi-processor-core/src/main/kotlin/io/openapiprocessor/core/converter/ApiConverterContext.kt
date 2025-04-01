/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.model.DataTypes
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.RefResolver

/**
 * helper to pass down parameters
 */
data class ApiConverterContext(
    val path: String,
    val method: HttpMethod,
    val dataTypes: DataTypes,
    val resolver: RefResolver,
    private val interfaces: Map<String, ContentTypeInterface>? = null
) {
    fun with(dataTypes: DataTypes): ApiConverterContext {
        return ApiConverterContext(
            path,
            method,
            dataTypes,
            resolver,
            interfaces)
    }

    fun with(interfaces: Map<String, ContentTypeInterface>): ApiConverterContext {
        return ApiConverterContext(
            path,
            method,
            dataTypes,
            resolver,
            interfaces)
    }

    fun getContentTypeInterface(contentType: String): ContentTypeInterface? {
        return interfaces?.get(contentType)
    }
}
