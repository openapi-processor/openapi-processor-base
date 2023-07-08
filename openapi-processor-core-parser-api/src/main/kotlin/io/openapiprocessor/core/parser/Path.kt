/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * OpenAPI Path abstraction.
 */
interface Path {

    fun getPath(): String
    fun getOperations(): List<Operation>

}
