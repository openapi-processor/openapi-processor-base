/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.parser.HttpMethod

/**
 * Provides the properties required to check if a [Mapping] applies to a
 * [io.openapiprocessor.core.converter.SchemaInfo].
 */
interface MappingSchema {

    fun getPath(): String
    fun getMethod(): HttpMethod
    fun getName(): String
    fun getContentType(): String

    fun getType(): String?
    fun getFormat(): String?

    fun isPrimitive(): Boolean
    fun isArray(): Boolean

    fun toStringSchema(): String {
        val parts = mutableListOf<String>()
        parts.add("path: ${getPath()}")
        parts.add("method: ${getMethod()}")
        parts.add("name: ${getName()}")
        parts.add("contentType: ${getContentType()}")

        if (getType() != null) {
            parts.add("type: ${getType()}")
        }

        if (getFormat() != null) {
            parts.add("format: ${getFormat()}")
        }

        if (isPrimitive()) {
            parts.add("primitive")
        }

        if (isArray()) {
            parts.add("array")
        }

        return parts.joinToString(", ")
    }
}
