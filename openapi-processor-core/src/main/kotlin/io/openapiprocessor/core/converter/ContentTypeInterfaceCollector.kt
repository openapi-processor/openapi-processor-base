/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.datatypes.SimpleDataType
import io.openapiprocessor.core.openapi.ContentType
import io.openapiprocessor.core.openapi.HttpMethod
import io.openapiprocessor.core.openapi.HttpStatus
import io.openapiprocessor.core.openapi.Response
import io.openapiprocessor.core.model.Response as ModelResponse

class ContentTypeInterfaceCollector(
    private val path: String,
    private val method: HttpMethod
) {

    fun collectContentTypeInterfaces(
        contentTypeResponses: Map<ContentType, Map<HttpStatus, Response>>,
        statusResultResponses: Map<HttpStatus, List<ModelResponse>>
    ): Map<ContentType, ContentTypeInterface> {
        val contentTypeInterfaces = mutableMapOf<ContentType, ContentTypeInterface>()

        contentTypeResponses.forEach { (contentType, statusResponse) ->
            if (statusResponse.size == 1) {
                return@forEach
            }

            var dataType: DataType? = null
            statusResponse.forEach { (status, _) ->
                val match = statusResultResponses[status]?.find { r -> r.contentType == contentType }
                if (match == null) {
                    return@forEach
                }

                if (dataType == null) {
                    dataType = match.responseType

                } else if (isAdditionalContentType(match, dataType)) {
                    contentTypeInterfaces[contentType] = ContentTypeInterface(path, method)
                }
            }
        }

        return contentTypeInterfaces
    }

    private fun isAdditionalContentType(match: ModelResponse, dataType: DataType): Boolean
        = match.responseType !== dataType && match.responseType !is SimpleDataType
}
