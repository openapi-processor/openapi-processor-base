/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v31

import io.openapiparser.model.v31.MediaType as MediaType31
import io.openapiparser.model.v31.RequestBody as RequestBody31
import io.openapiprocessor.core.openapi.MediaType as OpenApiMediaType
import io.openapiprocessor.core.openapi.RequestBody as OpenApiRequestBody

/**
 * openapi-parser RequestBody abstraction.
 */
class RequestBody(private val requestBody: RequestBody31): OpenApiRequestBody {

    override fun getRequired(): Boolean {
        return requestBody.required
    }

    override fun getContent(): Map<String, OpenApiMediaType> {
        val content = linkedMapOf<String, OpenApiMediaType>()
        requestBody.content.forEach { (key: String, entry: MediaType31) ->
            content[key] = MediaType(entry)
        }
        return content
    }

    override val description: String?
        get() = requestBody.description
}
