/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.model

enum class HttpMethod(val method: String) {
    GET ("get"),
    PUT ("put"),
    POST ("post"),
    DELETE ("delete"),
    OPTIONS ("options"),
    HEAD ("head"),
    PATCH ("patch"),
    TRACE ("trace"),
    QUERY ("query")
}
