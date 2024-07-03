/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.parser.HttpMethod

interface MappingQuery {
    val path: String?
    val method: HttpMethod?
    /**
     *  name, depends on context.
     *
     *  - parameter: name
     *  - request body: inline name
     *  - response: inline name
     *  - schema: name
     *  - property: name
     */
    val name: String?
    val format: String?
    val type: String?
    val contentType: String?
    val primitive: Boolean
    val array: Boolean
    val allowObject: Boolean  // use object @ annotation
}

@Deprecated(message = "migration")
class MappingQueryInfo(
    val info: MappingSchema
): MappingQuery {
    override val path: String
        get() = info.getPath()

    override val method: HttpMethod
        get() = info.getMethod()

    override val name: String
        get() = info.getName()

    override val format: String?
        get() = info.getFormat()

    override val type: String?
        get() = info.getType()

    override val contentType: String
        get() = info.getContentType()

    override val primitive: Boolean
        get() = info.isPrimitive()

    override val array: Boolean
        get() = info.isArray()

    override val allowObject: Boolean
        get() = TODO("Not yet implemented")
}

class MappingQueryValues(
    override val path: String? = null,
    override val method: HttpMethod? = null,
    /**
     *  name, depends on context.
     *
     *  - parameter: name
     *  - request body: inline name
     *  - response: inline name
     *  - schema: name
     *  - property: name
     */
    override val name: String? = null,
    override val format: String? = null,
    override val type: String? = null,
    override val contentType: String? = null,
    override val primitive: Boolean = false,
    override val array: Boolean = false,
    override val allowObject: Boolean = false  // use object @ annotation
): MappingQuery {

    override fun toString(): String {
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

        if (format != null) {
            parts.add("format: $format")
        }

        if (type != null) {
            parts.add("type: $type")
        }

        if (contentType != null) {
            parts.add("contentType: $contentType")
        }

        if (primitive) {
            parts.add("primitive")
        }

        if (array) {
            parts.add("array")
        }

        if (allowObject) {
            parts.add("allow object")
        }

        return parts.joinToString(", ")
    }
}
