/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * OpenAPI parser result abstraction.
 */
interface OpenApi {

    fun getServers(): List<Server>
    fun getPaths(): Map<String, Path>
    fun getSchemas(): Map<String, Schema>

    fun getRefResolver(): RefResolver

    fun printWarnings()
    fun hasWarnings(): Boolean

}
