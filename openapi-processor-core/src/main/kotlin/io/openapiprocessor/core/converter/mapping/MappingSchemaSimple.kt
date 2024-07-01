/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.parser.HttpMethod

class MappingSchemaSimple(
    private val path: String? = null,
    private val method: HttpMethod
): MappingSchema {
    override fun getPath(): String {
        return path ?: ""
    }

    override fun getMethod(): HttpMethod {
        return method
    }

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getContentType(): String {
        TODO("Not yet implemented")
    }

    override fun getType(): String? {
        TODO("Not yet implemented")
    }

    override fun getFormat(): String? {
        TODO("Not yet implemented")
    }

    override fun isPrimitive(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isArray(): Boolean {
        TODO("Not yet implemented")
    }
}
