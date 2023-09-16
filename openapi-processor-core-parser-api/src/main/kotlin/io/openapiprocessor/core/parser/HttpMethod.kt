/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

/**
 * http methods.
 */
enum class HttpMethod(val method: String) {
    DELETE ("delete"),
    GET ("get"),
    HEAD ("head"),
    OPTIONS ("options"),
    PATCH ("patch"),
    POST ("post"),
    PUT ("put"),
    TRACE ("trace")
}
