/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.test.parser.swagger

import io.openapiprocessor.core.parser.OpenApi
import io.openapiprocessor.core.parser.swagger.OpenApi as OpenApiSwagger
import io.swagger.v3.parser.OpenAPIV3Parser

fun parse(yaml: String): OpenApi {
    val result = OpenAPIV3Parser()
        .readContents (yaml)

    return OpenApiSwagger(result)
}
