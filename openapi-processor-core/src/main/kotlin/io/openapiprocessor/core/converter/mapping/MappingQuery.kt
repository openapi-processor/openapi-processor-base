/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

import io.openapiprocessor.core.parser.HttpMethod


interface MappingQueryEndpoint {
    val path: String?
        get() = null

    val method: HttpMethod?
        get() = null
}

interface MappingQueryType {
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
        get() = null

    val type: String?
        get() = null

    val format: String?
        get() = null

    // accept object @ annotation ?
    val allowObject: Boolean
        get() = false
}



interface MappingQuery : MappingQueryEndpoint,  MappingQueryType {
    val contentType: String?
        get() = null

    val primitive: Boolean
        get() = false

    val array: Boolean
        get() = false
}


@Deprecated(message = "migration, use MappingQueryX")
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
