/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.converter

/**
 * thrown when the ApiConverter hits a multipart/form-data or application/x-www-form-urlencoded
 * response body where the schema is not an object.
 */
class NoRequestBodySchemaException(private val path: String): RuntimeException() {

    override val message: String
        get() = "the schema of the request body of $path should be an object!"

}
