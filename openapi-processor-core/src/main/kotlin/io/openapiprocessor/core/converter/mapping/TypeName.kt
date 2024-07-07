/*
 * Copyright 2024 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter.mapping

fun splitTypeName(typeName: String): Pair<String, String?> {
    val split = typeName
            .split(":")
            .map { it.trim() }

    val type = split.component1()
    var format: String? = null
    if (split.size == 2) {
        format = split.component2()
    }

    return Pair(type, format)
}
