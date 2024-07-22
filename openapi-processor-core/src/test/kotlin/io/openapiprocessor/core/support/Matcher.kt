/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.support

import io.openapiprocessor.core.converter.mapping.matcher.TypeMatcher
import io.openapiprocessor.core.parser.HttpMethod

fun typeMatcher(
    path: String? = null,
    method: HttpMethod? = null,
    name: String? = null,
    contentType: String? = null,
    type: String? = null,
    format: String? = null,
    primitive: Boolean = false,
    array: Boolean = false
): TypeMatcher {
    return TypeMatcher(query(
        path,
        method,
        name,
        contentType,
        type,
        format,
        primitive,
        array
    ))
}
