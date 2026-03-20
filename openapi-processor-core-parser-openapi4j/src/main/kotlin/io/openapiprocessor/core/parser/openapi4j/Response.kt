/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.openapi4j

import io.openapiprocessor.core.openapi.MediaType as OpenApiMediaType
import io.openapiprocessor.core.openapi.Response as OpenApiResponse
import org.openapi4j.parser.model.v3.MediaType as O4jMediaType
import org.openapi4j.parser.model.v3.Response as O4jResponse

/**
 * openapi4j Response abstraction.
 */
class Response(private val response: O4jResponse, private val refResolver: RefResolverNative): OpenApiResponse {

    override fun getContent(): Map<String, OpenApiMediaType> {
        var resp = response
        if(resp.isRef) {
            resp = refResolver.resolve(resp)
        }

        val content = linkedMapOf<String, OpenApiMediaType>()
        resp.contentMediaTypes?.forEach { (key: String, entry: O4jMediaType) ->
            content[key] = MediaType(entry)
        }
        return content
    }

    override val description: String?
        get() {
            var resp = response
            if(resp.isRef) {
                resp = refResolver.resolve(resp)
            }

            return resp.description
        }
}
