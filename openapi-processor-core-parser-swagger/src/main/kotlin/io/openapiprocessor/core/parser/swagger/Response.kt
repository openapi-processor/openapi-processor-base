/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.openapi.MediaType as OpenApiMediaType
import io.openapiprocessor.core.openapi.Response as OpenApiResponse
import io.swagger.v3.oas.models.media.MediaType as SwaggerMediaType
import io.swagger.v3.oas.models.responses.ApiResponse as SwaggerResponse

/**
 * Swagger Response abstraction.
 */
class Response(private val response: SwaggerResponse): OpenApiResponse {

    override fun getContent(): Map<String, OpenApiMediaType> {
        val content = linkedMapOf<String, OpenApiMediaType>()
        response.content?.forEach { (key: String, value: SwaggerMediaType) ->
            content[key] = MediaType(value)
        }
        return content
    }

    override val description: String?
        get() = response.description

}
