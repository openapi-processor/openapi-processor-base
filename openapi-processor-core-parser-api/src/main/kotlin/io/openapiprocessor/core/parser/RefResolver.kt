/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * Resolves $ref objects from an OpenAPI.
 */
interface RefResolver {
    fun resolve(ref: Schema): NamedSchema
}

class NamedSchema(val name: String?, val schema: Schema) {
    val hasName = name != null
    val hasNoName = name == null
}

/**
 * Extract a name from the $ref that can be used to name classes.
 *
 * @param ref the $ref path
 */
fun getRefName(ref: String): String? {
    val split = ref.split('#')

    if (split.size == 1) {
        val fileName = ref.substring(ref.lastIndexOf('/') + 1)
        val lastDot = fileName.lastIndexOf('.')
        if (lastDot == -1) {
            return fileName
        }
        return fileName.take(lastDot)

    } else if (split.size > 1) {
        val hash = split[1]
        return hash.substring(hash.lastIndexOf('/') + 1)
    }

    return null
}
