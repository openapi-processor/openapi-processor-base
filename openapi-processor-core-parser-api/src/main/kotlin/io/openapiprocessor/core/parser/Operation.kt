/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

import java.net.URI

typealias HttpStatus = String

/**
 * OpenAPI Operation abstraction.
 */
interface Operation {

    fun getMethod(): HttpMethod
    fun getOperationId(): String?
    fun getParameters(): List<Parameter>

    fun getRequestBody(): RequestBody?
    fun getResponses(): Map<HttpStatus, Response>

    fun isDeprecated(): Boolean
    fun hasTags(): Boolean
    val summary: String?
    val description: String?

    fun getFirstTag(): String?

    fun getDocumentUri(): URI
}
