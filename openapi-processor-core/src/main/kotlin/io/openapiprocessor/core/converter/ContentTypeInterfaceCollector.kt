/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.parser.ContentType
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.parser.HttpStatus
import io.openapiprocessor.core.parser.Response

class ContentTypeInterfaceCollector(
    private val path: String,
    private val method: HttpMethod,
    responseCollector: ContentTypeResponseCollector
) {
    val contentTypeInterfaces: Map<String, ContentTypeInterface> = collectInterfaces(
        responseCollector.contentTypeResponses
    )

    private fun collectInterfaces(contentTypeResponses: Map<ContentType, Map<HttpStatus, Response>>)
    : Map<String, ContentTypeInterface> {
        val contentTypeInterfaces = mutableMapOf<String, ContentTypeInterface>()

        contentTypeResponses.forEach { (contentType, statusResponses) ->
            if (statusResponses.size > 1) {
                contentTypeInterfaces[contentType] = ContentTypeInterface(path, method)
            }
        }

        return contentTypeInterfaces
    }
}
