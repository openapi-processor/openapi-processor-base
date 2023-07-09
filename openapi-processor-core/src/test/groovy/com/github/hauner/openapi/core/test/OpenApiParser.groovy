/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package com.github.hauner.openapi.core.test

import io.openapiprocessor.core.parser.OpenApi
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.core.support.OpenApiParserKt

/**
 * OpenAPI parser wrapper.
 *
 * provide default parameter to groovy
 */
class OpenApiParser {
    static OpenApi parse(String apiYaml, ParserType parserType = ParserType.SWAGGER) {
        return OpenApiParserKt.parse(apiYaml, parserType)
    }
}
