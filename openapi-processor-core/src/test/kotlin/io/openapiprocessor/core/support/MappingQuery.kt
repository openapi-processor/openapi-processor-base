/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.MappingQuery as Query
import io.openapiprocessor.core.converter.mapping.MappingQuery
import io.openapiprocessor.core.converter.mapping.MappingSchemaPlain
import io.openapiprocessor.core.parser.HttpMethod

// todo use MappingQuery()
fun query(
    path: String? = null,
    method: HttpMethod? = null,
    name: String? = null,
    contentType: String? = null,
    type: String? = null,
    format: String? = null,
    primitive: Boolean = false,
    array: Boolean = false
): MappingQuery {
    return Query(
        MappingSchemaPlain(
            path,
            method,
            name,
            contentType,
            type,
            format,
            primitive,
            array
        )
    )
}
