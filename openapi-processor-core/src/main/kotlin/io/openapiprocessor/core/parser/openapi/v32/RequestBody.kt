/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.openapiparser.model.v32.MediaType as MediaType32
import io.openapiparser.model.v32.RequestBody as RequestBody32
import io.openapiprocessor.core.openapi.MediaType as OpenApiMediaType
import io.openapiprocessor.core.openapi.RequestBody as OpenApiRequestBody

/**
 * openapi-parser RequestBody abstraction.
 */
class RequestBody(private val requestBody: RequestBody32): OpenApiRequestBody {

    override fun getRequired(): Boolean {
        return requestBody.required
    }

    override fun getContent(): Map<String, OpenApiMediaType> {
        val content = linkedMapOf<String, OpenApiMediaType>()
        requestBody.content.forEach { (key: String, entry: MediaType32) ->
            content[key] = MediaType(entry)
        }
        return content
    }

    override val description: String?
        get() = requestBody.description
}
