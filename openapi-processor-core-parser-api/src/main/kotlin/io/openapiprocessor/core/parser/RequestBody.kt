/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * OpenAPI RequestBody abstraction.
 */
interface RequestBody {

    // default: false
    fun getRequired(): Boolean
    fun getContent(): Map<String, MediaType>

}
