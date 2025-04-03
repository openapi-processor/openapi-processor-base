/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

typealias ContentType = String

/**
 * OpenAPI Response abstraction.
 */
interface Response {

    fun getContent(): Map<ContentType, MediaType>
    val description: String?

}
