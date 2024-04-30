/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi.v30

import io.openapiparser.model.v30.MediaType as MediaType30
import io.openapiparser.model.v30.Response as Response30
import io.openapiprocessor.core.parser.MediaType as ParserMediaType
import io.openapiprocessor.core.parser.Response as ParserResponse

/**
 * openapi-parser Response abstraction.
 */
class Response(private val response: Response30): ParserResponse {

    override fun getContent(): Map<String, ParserMediaType> {
        val responseContent = if(response.isRef) {
            response.refObject.content
        } else {
            response.content
        }

        val content = linkedMapOf<String, ParserMediaType>()
        responseContent.forEach { (key: String, entry: MediaType30) ->
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
