/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.processor

class BadMappingException(private val mapping: String): RuntimeException() {

    override val message: String
        get() = "the mapping '$mapping' is not allowed here!"
}
