/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v32

import io.openapiparser.model.v32.MediaType as MediaType32
import io.openapiparser.model.v32.RequestBody as RequestBody32
import io.openapiprocessor.core.parser.MediaType as ParserMediaType
import io.openapiprocessor.core.parser.RequestBody as ParserRequestBody

/**
 * openapi-parser RequestBody abstraction.
 */
class RequestBody(private val requestBody: RequestBody32): ParserRequestBody {

    override fun getRequired(): Boolean {
        return requestBody.required
    }

    override fun getContent(): Map<String, ParserMediaType> {
        val content = linkedMapOf<String, ParserMediaType>()
        requestBody.content.forEach { (key: String, entry: MediaType32) ->
            content[key] = MediaType(entry)
        }
        return content
    }

    override val description: String?
        get() = requestBody.description
}
