/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser

import io.openapiprocessor.core.openapi.OpenApi

interface Parser {
    fun parse(apiPath: String): OpenApi
}
