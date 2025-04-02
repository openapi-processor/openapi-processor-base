/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

import io.openapiprocessor.core.parser.ContentType
import io.openapiprocessor.core.parser.HttpStatus
import io.openapiprocessor.core.parser.Response
import io.openapiprocessor.core.processor.mapping.v2.ResultStyle


private const val EMPTY: String = ""


/**
 * collect responses by content type to filter & select the final response data type.
 *
 * multiple & all     => Object  if different result types
 * multiple & success => Marker  if different result types
 *
 *    content type => success 2x
 *                    errors  4x
 *
 *    content type => success 2x  Result A  => marker interface or Object (depends on result style)
 *                    success 2x  Result B  => marker interface or Object (depends on result style)
 *                    errors  4x
 *
 *    content type => success 2x  Result A
 *                    success 2x  void
 *                    errors  4x
 */
class ContentTypeResponseCollector(responses: Map<HttpStatus, Response>, private val resultStyle: ResultStyle) {
    val contentTypeResponses: Map<ContentType, Map<HttpStatus, Response>> = collectResponses(responses)

    private fun collectResponses(responses: Map<HttpStatus, Response>): Map<ContentType, Map<HttpStatus, Response>> {
        val contentTypeResponses = mutableMapOf<ContentType, MutableMap<HttpStatus, Response>>()

        responses.forEach { (httpStatus, response) ->
            val contents = response.getContent()

            if (resultStyle == ResultStyle.SUCCESS && isError(httpStatus)) {
                return@forEach
            }

            contents.keys.forEach { contentType ->
                var srs = contentTypeResponses[contentType]
                if (srs == null) {
                    srs = mutableMapOf()
                    contentTypeResponses[contentType] = srs
                }

                srs[httpStatus] = response
            }

            // no result
            if (contents.isEmpty()) {
                var srs = contentTypeResponses[EMPTY]
                if (srs == null) {
                    srs = mutableMapOf()
                    contentTypeResponses[EMPTY] = srs
                }

                srs[httpStatus] = response
            }
        }

        return contentTypeResponses
    }

    private fun isError(status: HttpStatus): Boolean {
        return status.startsWith("4")
            || status.startsWith("5")
    }
}
