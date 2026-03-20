/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.openapiparser.model.v32.MediaType as MediaType32
import io.openapiparser.model.v32.Response as Response32
import io.openapiprocessor.core.openapi.MediaType as OpenApiMediaType
import io.openapiprocessor.core.openapi.Response as OpenApiResponse

/**
 * openapi-parser Response abstraction.
 */
class Response(private val response: Response32): OpenApiResponse {

    override fun getContent(): Map<String, OpenApiMediaType> {
        val responseContent = if(response.isRef) {
            response.refObject.content
        } else {
            response.content
        }

        val content = linkedMapOf<String, OpenApiMediaType>()
        responseContent.forEach { (key: String, entry: MediaType32) ->
            content[key] = MediaType(entry)
        }
        return content
    }

    override val description: String
        get() {
            return if (response.isRef) {
                response.refObject.description
            } else {
                response.description
            }
        }
}
