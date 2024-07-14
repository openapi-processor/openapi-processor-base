/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.parser.HttpMethod

class MappingSchemaPlain(
    private val path: String? = null,
    private val method: HttpMethod? = null,
    private val name: String? = null,
    private val contentType: String? = null,
    private val type: String? = null,
    private val format: String? = null,
    private val primitive: Boolean = false,
    private val array: Boolean = false
): MappingSchema {
    override fun getPath(): String {
        return path ?: ""
    }

    override fun getMethod(): HttpMethod {
        return method ?: HttpMethod.GET
    }

    override fun getName(): String {
        return name ?: ""
    }

    override fun getContentType(): String {
        return contentType ?: ""
    }

    override fun getType(): String? {
        return type
    }

    override fun getFormat(): String? {
        return format
    }

    override fun isPrimitive(): Boolean {
        return primitive
    }

    override fun isArray(): Boolean {
        return array
    }
}
