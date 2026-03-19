/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.openapi

class Encoding(val contentType: String? = null)

/**
 * OpenAPI Parameter abstraction.
 */
interface Parameter {

    fun getIn(): String
    fun getName(): String

    fun getSchema(): Schema
    fun isRequired(): Boolean
    fun isDeprecated(): Boolean

    val description: String?

}