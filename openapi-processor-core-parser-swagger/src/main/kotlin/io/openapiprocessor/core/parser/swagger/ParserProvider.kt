/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.core.parser.swagger

import io.openapiprocessor.core.parser.Parser
import io.openapiprocessor.core.parser.ParserProvider

class ParserProvider : ParserProvider {
    override fun getName(): String {
        return "SWAGGER"
    }

    override fun getParser(): Parser {
        return Parser()
    }
}
