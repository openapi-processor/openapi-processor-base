/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.MappingQuery
import io.openapiprocessor.core.converter.mapping.splitTypeName
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.parser.HttpMethod

class MappingQueryX(
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
    override val type: String? = null,
    override val format: String? = null,
    override val contentType: String? = null,
    override val primitive: Boolean = false,
    override val array: Boolean = false,
    /** use object @ annotation */
    override val allowObject: Boolean = false
): MappingQuery {

    companion object {
        // callable like constructor
        operator fun invoke(endpoint: Endpoint): MappingQuery {
            return MappingQueryX(
                endpoint.path,
                endpoint.method
            )
        }

        // callable like constructor
        operator fun invoke(endpoint: Endpoint, parameter: Parameter): MappingQuery {
            val (type, format) = splitTypeName(parameter.dataType.getSourceName())

            return MappingQueryX(
                endpoint.path,
                endpoint.method,
                parameter.name,
                type,
                format
            )
        }

        // callable like constructor
        operator fun invoke(info: SchemaInfo): MappingQuery {
            val (type, format) = splitTypeName(info.getTypeFormat())

            return MappingQueryX(
                info.getPath(),
                info.getMethod(),
                info.getName(),
                type,
                format,
                info.getContentType(),
                info.isPrimitive(),
                info.isArray()
            )
        }
    }

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
