/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.converter.mapping.MappingQuery
import io.openapiprocessor.core.converter.mapping.MappingSchema
import io.openapiprocessor.core.converter.mapping.splitTypeName
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.parser.HttpMethod

class MappingFinderQuery(
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
            return MappingFinderQuery(
                endpoint.path,
                endpoint.method
            )
        }

        // callable like constructor
        operator fun invoke(endpoint: Endpoint, parameter: Parameter): MappingQuery {
            val (type, format) = splitTypeName(parameter.dataType.getSourceName())

            return MappingFinderQuery(
                endpoint.path,
                endpoint.method,
                parameter.name,
                type,
                format
            )
        }

        // callable like constructor
        operator fun invoke(info: MappingSchema): MappingQuery {
            return MappingFinderQuery(
                info.getPath(),
                info.getMethod(),
                info.getName(),
                info.getType(),
                info.getFormat(),
                info.getContentType(),
                info.isPrimitive(),
                info.isArray()
            )
        }
    }

    override fun toString(): String {
        val parts = mutableListOf<String>()

        if (name != null) {
            parts.add("name: '$name'")
        }

        if (path != null) {
            var part = "path: "

            if (method != null) {
                part += "$method "
            }

            part += "'$path'"
            parts.add(part)
        }

        if (!contentType.isNullOrBlank()) {
            parts.add("$contentType")
        }

        if (type != null) {
            var part = "type: '$type"
            if (format != null) {
                part += ":$format"
            }
            part += "'"
            parts.add(part)
        }

        // flags
        var flags = ""

        if (primitive) {
            flags += "P"
        }

        if (array) {
            flags += "A"
        }

        if (allowObject) {
            flags += "O"
        }

        if (flags.isNotEmpty()) {
            parts.add(flags)
        }

        return parts.joinToString(" ")
    }
}
