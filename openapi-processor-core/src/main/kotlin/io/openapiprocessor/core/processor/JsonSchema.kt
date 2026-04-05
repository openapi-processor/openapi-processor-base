/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

import java.net.URI

class JsonSchema(val uriTemplate: String, val schemaTemplate: String) {
    fun getUri(version: String): URI {
        return URI(uriTemplate.replace($$"$version", version))
    }

    fun getSchema(version: String): String {
        return schemaTemplate.replace($$"$version", version)
    }
}


