/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * OpenAPI MediaType abstraction.
 */
interface MediaType {
    fun getSchema(): Schema
    val encodings: Map<String, Encoding>
}
