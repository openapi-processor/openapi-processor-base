/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.mapping.MappingSchema
import io.openapiprocessor.core.parser.HttpMethod

class MappingSchema(
    private val path: String? = null,
    private val method: HttpMethod? = null,
    private val name: String? = null,
    private val contentType: String? = null,
    private val type: String? = null,
    private val format: String? = null,
): MappingSchema {
    override fun getPath(): String {
        return path!!
    }

    override fun getMethod(): HttpMethod {
        return method!!
    }

    override fun getName(): String {
        return name!!
    }

    override fun getContentType(): String {
        return contentType!!
    }

    override fun getType(): String? {
        return type
    }

    override fun getFormat(): String? {
        return format
    }

    override fun isPrimitive(): Boolean {
        return listOf("boolean", "integer", "number", "string").contains(type)
    }

    override fun isArray(): Boolean {
        return type.equals("array")
    }

    override fun toStringSchema(): String {
        val parts = mutableListOf<String>()
        if (path != null) {
            parts.add("path: $path")
        }

        if (method != null) {
            parts.add("method: $method")
        }

        if (name != null) {
            parts.add("name: $name")
        }

        if (contentType != null) {
            parts.add("contentType: $contentType")
        }

        if (type != null) {
            parts.add("type: $type")
        }

        if (format != null) {
            parts.add("format: $format")
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
